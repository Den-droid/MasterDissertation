import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { UserAssignmentDto, AssignmentDto, AssignmentAnswerDto, AssignmentResponseDto, AssignDto } from "../models/assignment.model";
import { AnswerDto } from "../models/answer.model";

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {
  private url: string = baseUrl + '/assignments';

  constructor(private readonly httpClient: HttpClient) {
  }

  getByUserId(userId: number): Observable<UserAssignmentDto[]> {
    const options = {
      params: new HttpParams().set('userId', userId.toString())
    };
    return this.httpClient.get<UserAssignmentDto[]>(`${this.url}/getByUserId`, options);
  }

  getById(userAssignmentId: number): Observable<AssignmentDto> {
    return this.httpClient.get<AssignmentDto>(`${this.url}/${userAssignmentId}`);
  }

  assign(assignDto: AssignDto): Observable<any> {
    return this.httpClient.post(`${this.url}/assign`, assignDto);
  }

  startContinue(userAssignmentId: number): Observable<any> {
    return this.httpClient.put(`${this.url}/${userAssignmentId}/startContinue`, {});
  }

  finish(userAssignmentId: number): Observable<any> {
    return this.httpClient.put(`${this.url}/${userAssignmentId}/finish`, {});
  }

  answer(userAssignmentId: number, assignmentAnswerDto: AssignmentAnswerDto): Observable<AssignmentResponseDto> {
    return this.httpClient.post<AssignmentResponseDto>(
      `${this.url}/${userAssignmentId}/giveAnswer`,
      assignmentAnswerDto
    );
  }

  getAnswers(userAssignmentId: number): Observable<AnswerDto[]> {
    return this.httpClient.get<AnswerDto[]>(`${this.url}/${userAssignmentId}/answers`);
  }
}
