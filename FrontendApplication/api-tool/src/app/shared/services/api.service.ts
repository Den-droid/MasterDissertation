import { Injectable } from "@angular/core";
import { baseUrl } from "../constants/url.constant";
import { mapMethodTypeToLabel, MethodType } from "../constants/method-type.constant";
import { Observable } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private url: string = baseUrl;

  constructor(private readonly httpClient: HttpClient) {
  }

  sendRequest(url: string, method: MethodType, paramsOrBody: any): Observable<any> {
    if (method == MethodType.GET || method == MethodType.DELETE) {
      let params = new HttpParams({ fromObject: paramsOrBody });
      return this.httpClient.request(mapMethodTypeToLabel(method), `${this.url}${url}`,
        { params: params, observe: 'response' });
    } else {
      return this.httpClient.request(mapMethodTypeToLabel(method), `${this.url}${url}`, {
        body: paramsOrBody,
        observe: 'response'
      });
    }
  }
}
