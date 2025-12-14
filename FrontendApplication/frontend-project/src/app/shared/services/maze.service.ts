import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { baseUrl } from "../constants/url.constant";
import { IdDto } from "../models/common.model";
import { AddMazeDto } from "../models/maze.model";

@Injectable({
    providedIn: 'root'
})
export class MazeService {
    private url: string = baseUrl + "/mazes";

    constructor(private readonly httpClient: HttpClient) {
    }

    add(dto: AddMazeDto): Observable<IdDto> {
        return this.httpClient.post<IdDto>(`${this.url}`, dto);
    }
}