package exchange.estimation.services.implementations;

import exchange.estimation.models.entities.Exchanges;
import exchange.estimation.models.entities.Modifies;
import exchange.estimation.models.repositories.ExchangesRepository;
import exchange.estimation.services.interfaces.ExchangesService;
import exchange.estimation.services.interfaces.ModifiesService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Service
public class ExchangesServiceImpl implements ExchangesService {
    private static final String EXCHANGE_ENDPOINT = "https://bnr.ro/files/xml/years/nbrfxrates";

    private final ModifiesService modifiesService;
    private final ExchangesRepository exchangesRepository;
    private final RestTemplate restTemplate;

    public ExchangesServiceImpl(ModifiesService modifiesService, ExchangesRepository exchangesRepository, RestTemplateBuilder restTemplateBuilder) {
        this.modifiesService = modifiesService;
        this.exchangesRepository = exchangesRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public void GetGraph(String type, Long number, String currency, String methode) throws Exception {
        try {
            Modifies modify = modifiesService.GetLastModify();

            Calendar cal = Calendar.getInstance();
            LocalDate lastSavedDate = modify.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            cal.set(Calendar.YEAR, lastSavedDate.getYear());
            cal.set(Calendar.MONTH, 11);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            cal.set(Calendar.HOUR_OF_DAY, 00);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
            cal.set(Calendar.MILLISECOND, 00);
            Date endOfSavedYear = cal.getTime();

            cal.set(Calendar.YEAR, lastSavedDate.getYear() + 1);
            cal.set(Calendar.DAY_OF_YEAR, 1);
            Date startDateOfNewYear = cal.getTime();

            Date currentDate = new Date();
            int startYear = lastSavedDate.getYear();
            int endYear = lastSavedDate.getYear();

            if (currentDate.after(modify.getDate()) && currentDate.before(endOfSavedYear)) {
                endYear++;
            } else if (currentDate.after(startDateOfNewYear) && modify.getDate().before(endOfSavedYear)) {
                endYear += 2;
            } else if (currentDate.after(startDateOfNewYear) && currentDate.after(endOfSavedYear)) {
                endYear += 2;
                startYear += 1;
            }
            Date lastDate = null;
            Document doc;

            for (int year = startYear; year < endYear; year++) {
                ResponseEntity<?> responseEntity = GetExchanges(year);
                if (responseEntity == null) {
                    continue;
                }

                doc = convertStringToXMLDocument(responseEntity.getBody().toString());
                NodeList cubeList = doc.getElementsByTagName("Cube");

                for (int i = 0; i < cubeList.getLength(); i++) {
                    Node cube = cubeList.item(i);
                    DateFormat formatter;
                    Date date = null;

                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date = formatter.parse(cube.getAttributes().getNamedItem("date").getNodeValue());

                        if (!date.after(modify.getDate())) {
                            continue;
                        }
                        lastDate = date;
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    NodeList rateList = cube.getChildNodes();
                    PostLatestExchanges(rateList, date);
                }

                Modifies new_modify = new Modifies(lastDate);
                modifiesService.PostModify(new_modify);
            }
        } catch (Exception e) {
            Date lastDate = null;
            Document doc;

            for (int year = 2005; year <= Calendar.getInstance().get(Calendar.YEAR); year++) {
                ResponseEntity<?> responseEntity = GetExchanges(year);
                if (responseEntity == null) {
                    continue;
                }

                doc = convertStringToXMLDocument(responseEntity.getBody().toString());
                NodeList cubeList = doc.getElementsByTagName("Cube");

                for (int i = 0; i < cubeList.getLength(); i++) {
                    Node cube = cubeList.item(i);
                    DateFormat formatter;
                    Date date = null;
                    formatter = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        date = formatter.parse(cube.getAttributes().getNamedItem("date").getNodeValue());
                        lastDate = date;
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }

                    NodeList rateList = cube.getChildNodes();
                    PostLatestExchanges(rateList, date);
                }
            }
            Modifies modify = new Modifies(lastDate);
            modifiesService.PostModify(modify);
        }

        ReadFromFile(type, number, currency, methode);
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();

            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void PostLatestExchanges(NodeList rateList, Date date) {
        for (int j = 1; j < rateList.getLength(); j += 2) {
            Node rate = rateList.item(j);
            String currency = rate.getAttributes().getNamedItem("currency").getNodeValue();
            double value;
            try {
                value = Double.parseDouble(rate.getFirstChild().getNodeValue());
            } catch (Exception ex) {
                value = 0.0;
            }
            Exchanges exchange = new Exchanges(date, value, currency);
            exchangesRepository.save((exchange));
        }
    }

    public ResponseEntity<?> GetExchanges(int year) {
        ResponseEntity<?> responseEntity =
                this.restTemplate.getForEntity(EXCHANGE_ENDPOINT + year + ".xml", String.class, 1);

        if (responseEntity.getStatusCode() != HttpStatus.OK
                || !responseEntity.getHeaders().getContentType().toString().contains("xml")) {
            return null;
        }
        return responseEntity;
    }

    private void ReadFromFile(String type, Long number, String currency, String methode) throws Exception {
        String path = "E:\\an4\\EP\\EP-Project\\exchange-estimation\\exchange-estimation-web\\src\\main\\resources\\script\\main.exe";
        Process p;

        ProcessBuilder pb = new ProcessBuilder(path, type + "~" + number + "~" + currency + "~" + methode);
        int exitCode = 0;
        try {
            p = pb.start();
            exitCode = p.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (exitCode != 0) {
            throw new Exception("Python script failed!");
        }
    }
}
