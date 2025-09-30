import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { MethodTypeDto, UrlDto } from "../models/url.model";

@Injectable({
  providedIn: 'root'
})
export class UrlService {
  private url: string = baseUrl + "/api/urls";

  constructor(private readonly httpClient: HttpClient) {
  }

  getAll(): Observable<UrlDto[]> {
    return this.httpClient.get<UrlDto[]>(`${this.url}`);
  }

  getByUrl(url: string, method : number): Observable<UrlDto[]> {
    const params = new HttpParams()
      .set('url', url)
      .set('method', method);
    return this.httpClient.get<UrlDto[]>(`${this.url}`, { params });
  }

  getMethods(): Observable<MethodTypeDto[]> {
    return this.httpClient.get<MethodTypeDto[]>(`${this.url}/methods`);
  }
}
