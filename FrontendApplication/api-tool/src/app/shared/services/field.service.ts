import { Observable } from "rxjs";
import { FieldDto } from "../models/field.model";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { baseUrl } from "../constants/url.constant";

@Injectable({
  providedIn: 'root'
})
export class FieldService {
  private url: string = baseUrl + "/api/fields";

  constructor(private readonly httpClient: HttpClient) {
  }

  getByUrlId(urlId: number): Observable<FieldDto[]> {
    let params = new HttpParams()
      .set('urlId', urlId);
    return this.httpClient.get<FieldDto[]>(`${this.url}`, { params });
  }
}
