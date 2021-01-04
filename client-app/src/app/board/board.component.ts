import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.css'],
})
export class BoardComponent implements OnInit {
  selectedEstimationTime = 'Day';
  estimatedTime = 1;
  selectedCurency = 'EUR';
  selectedEstimationMethod = 'Linear regression';

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {}

  getGraphic() {
    if (
      this.selectedEstimationTime === 'Day' &&
      !(this.estimatedTime >= 1 && this.estimatedTime <= 10)
    ) {
      alert('Number of Days should be between 1 and 10');
      return;
    }
    if (
      this.selectedEstimationTime === 'Month' &&
      !(this.estimatedTime >= 1 && this.estimatedTime <= 11)
    ) {
      alert('Number of Months should be between 1 and 11');
      return;
    }
    if (
      this.selectedEstimationTime === 'Year' &&
      !(this.estimatedTime >= 1 && this.estimatedTime <= 5)
    ) {
      alert('Number of Years should be between 1 and 5');
      return;
    }

    this.httpClient.get(
      `http://localhost:5677/api/exchanges` +
        `/type/${this.selectedEstimationTime}` +
        `/number/${this.estimatedTime}` +
        `/currency/${this.selectedCurency}` +
        `/methode/${this.selectedEstimationMethod}`
    ).subscribe((obj)=> {
    }, (err) => {
    })
  }
}
