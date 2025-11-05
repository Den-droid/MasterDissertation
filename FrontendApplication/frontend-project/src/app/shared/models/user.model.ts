import { UniversityDto } from "./university.model";

export class ApiKeyDto {
  constructor(public apiKey: string) { }
}

export class UserDto {
  constructor(public id: number, public firstName: string, public lastName: string, public email: string,
    public role: string, public isApproved: boolean, public university: UniversityDto
  ) {

  }
}