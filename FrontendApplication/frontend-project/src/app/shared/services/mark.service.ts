import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { MarkAssignmentDto, AssignmentsToMarkDto } from "../models/mark.model";

@Injectable({
  providedIn: 'root'
})
export class MarkService {

  private url: string = baseUrl;

  constructor(private readonly httpClient: HttpClient) {
  }

  mark(assignmentId: number, markAssignmentDto: MarkAssignmentDto): Observable<any> {
    return this.httpClient.put(`${this.url}/assignments/${assignmentId}/mark`, markAssignmentDto);
  }

  getAssignmentsToMark(userId: number): Observable<AssignmentsToMarkDto[]> {
    const options = {
      params: new HttpParams().set('userId', userId.toString())
    };
    return this.httpClient.get<AssignmentsToMarkDto[]>(`${this.url}/assignments/toMark`, options);
  }
}
