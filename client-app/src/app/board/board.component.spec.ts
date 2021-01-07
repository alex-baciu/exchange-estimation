import { HttpClient } from '@angular/common/http';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BoardComponent } from './board.component';
import { of } from 'rxjs';

describe('BoardComponent', () => {
  let component: BoardComponent;
  let fixture: ComponentFixture<BoardComponent>;
  const mockHttpClient = jasmine.createSpyObj(['get']);

  //initializare componenta, serviciu http mocked
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BoardComponent],
      providers: [{ provide: HttpClient, useValue: mockHttpClient }],
    }).compileComponents();
    fixture = TestBed.createComponent(BoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    mockHttpClient.get.and.returnValue(of(true));
  });

  //verifcare componenta creata
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  //verificare setare implicita a variabilelor
  it('should set the variables to default values', () => {
    expect(component.estimatedTime).toEqual(1);
    expect(component.selectedEstimationTime).toEqual('Day');
    expect(component.selectedCurency).toEqual('EUR');
    expect(component.selectedEstimationMethod).toEqual('Linear regression');
  });

  describe('getGraphic', () => {
    //verificare instiintare user in caz de valori gresite pentru zi
    it('should alert user if the value for selectedEstimationTime = Day is not correct', () => {
      spyOn(window, 'alert');
      component.selectedEstimationTime = 'Day';
      component.estimatedTime = 11;

      component.getGraphic();

      expect(window.alert).toHaveBeenCalledWith(
        'Number of Days should be between 1 and 10'
      );
    });

    //verificare instiintare user in caz de valori gresite pentru luna
    it('should alert user if the value for selectedEstimationTime = Month is not correct', () => {
      spyOn(window, 'alert');
      component.selectedEstimationTime = 'Month';
      component.estimatedTime = 13;

      component.getGraphic();

      expect(window.alert).toHaveBeenCalledWith(
        'Number of Months should be between 1 and 11'
      );
    });

    //verificare instiintare user in caz de valori gresite pentru an
    it('should alert user if the value for selectedEstimationTime = Year is not correct', () => {
      spyOn(window, 'alert');
      component.selectedEstimationTime = 'Year';
      component.estimatedTime = 10;

      component.getGraphic();

      expect(window.alert).toHaveBeenCalledWith(
        'Number of Years should be between 1 and 5'
      );
    });

    //verificare apel call http cu parametri corecti in caz de date corecte
    it('should alert user if the value for selectedEstimationTime = Year is not correct', () => {
      spyOn(window, 'alert');
      component.selectedEstimationTime = 'Month';
      component.estimatedTime = 3;
      component.selectedEstimationMethod = 'Linear regression';
      component.selectedCurency = 'USD';

      component.getGraphic();

      expect(mockHttpClient.get).toHaveBeenCalledWith(
        'http://localhost:5677/api/exchanges' +
          `/type/${component.selectedEstimationTime}` +
          `/number/${component.estimatedTime}` +
          `/currency/${component.selectedCurency}` +
          `/methode/${component.selectedEstimationMethod}`
      );
    });
  });

  it('should get data when click on interface button if query values are correct', async(() => {
    let buttonElement = fixture.debugElement.nativeElement.querySelector(
      '.send-button'
    );
    component.selectedEstimationTime = 'Month';
    component.estimatedTime = 3;
    component.selectedEstimationMethod = 'Linear regression';
    component.selectedCurency = 'USD';

    buttonElement.click();

    expect(mockHttpClient.get).toHaveBeenCalledWith(
      'http://localhost:5677/api/exchanges' +
        `/type/${component.selectedEstimationTime}` +
        `/number/${component.estimatedTime}` +
        `/currency/${component.selectedCurency}` +
        `/methode/${component.selectedEstimationMethod}`
    );
  }));
});
