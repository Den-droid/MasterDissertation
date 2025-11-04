import { SubjectDto } from "./subject.model";

export class AddFunctionDto {
    public constructor(public text: string, public variablesCount: number, public minValues: number[], 
        public maxValues: number[], public subjectId: number
    ) { }
}

export class UpdateFunctionDto {
    public constructor(public text: string, public variablesCount: number, public minValues: number[], 
        public maxValues: number[], public subjectId: number
    ) { }
}

export class FunctionDto {
    public constructor(public id: number, public text: string, public variablesCount: number, 
        public minValues: number[], public maxValues: number[], public subject: SubjectDto
    ) { }
}