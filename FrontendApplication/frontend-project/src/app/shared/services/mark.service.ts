import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { MarkDto } from "../models/mark.model";

@Injectable({
  providedIn: 'root'
})
export class MarkService {
  private url: string = baseUrl;

  constructor(private readonly httpClient: HttpClient) {
  }

  mark(assignmentId: number, markAssignmentDto: MarkDto): Observable<any> {
    return this.httpClient.put(`${this.url}/assignments/${assignmentId}/putMark`, markAssignmentDto);
  }

  getByUserAssignmentId(userAssignmentId : number): Observable<MarkDto[]> {
    return this.httpClient.get<MarkDto[]>(`${this.url}/assignments/${userAssignmentId}/marks`);
  }
}
