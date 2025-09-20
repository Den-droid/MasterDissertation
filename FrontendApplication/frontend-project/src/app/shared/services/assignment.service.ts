import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { UserAssignmentDto, AssignmentDto, AssignmentAnswerDto, AssignmentResponseDto } from "../models/assignment.model";
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
    return this.httpClient.get<UserAssignmentDto[]>(this.url + '/getByUserId', options);
  }

  getById(assignmentId: number): Observable<AssignmentDto> {
    return this.httpClient.get<AssignmentDto>(`${this.url}/${assignmentId}`);
  }

  isAvailable(userId: number): Observable<boolean> {
    const options = {
      params: new HttpParams().set('userId', userId.toString())
    };
    return this.httpClient.get<boolean>(this.url + '/isAvailable', options);
  }

  assign(userId: number): Observable<any> {
    const options = {
      params: new HttpParams().set('userId', userId.toString())
    };
    return this.httpClient.post(this.url + '/assign', null, options);
  }

  startContinue(assignmentId: number): Observable<any> {
    return this.httpClient.put(`${this.url}/${assignmentId}/startContinue`, {});
  }

  stop(assignmentId: number): Observable<any> {
    return this.httpClient.put(`${this.url}/${assignmentId}/stop`, {});
  }

  finish(assignmentId: number): Observable<any> {
    return this.httpClient.put(`${this.url}/${assignmentId}/finish`, {});
  }

  answer(assignmentId: number, assignmentAnswerDto: AssignmentAnswerDto): Observable<AssignmentResponseDto> {
    return this.httpClient.post<AssignmentResponseDto>(
      `${this.url}/${assignmentId}/answer`,
      assignmentAnswerDto
    );
  }

  getAnswers(assignmentId: number): Observable<AnswerDto[]> {
    return this.httpClient.get<AnswerDto[]>(`${this.url}/${assignmentId}/answers`);
  }

  getAnswersForAssignments(assignmentIds: number[]): Observable<AnswerDto[][]> {
    let params = new HttpParams();

    assignmentIds.forEach(id => {
      params = params.append('assignmentIds', id.toString());
    });
    return this.httpClient.get<AnswerDto[][]>(`${this.url}/answers`, { params });
  }
}
