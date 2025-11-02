import { UniversityDto } from "./university.model";

export class SubjectDto {
    public constructor(public id: number, public name: string, public university: UniversityDto) { }
}

export class AddSubjectDto {
    public constructor(public name: string, public universityId: number) {
    }
}

export class UpdateSubjectDto {
    public constructor(public name: string, public universityId: number) {
    }
}