import { AssignmentRestrictionType } from "../constants/assignment-restriction-type";
import { AssignmentStatus } from "../constants/assignment-status.constant";
import { FunctionResultType } from "../constants/function-result-type.constant";
import { UserDto } from "./user.model";

export class AssignmentAnswerDto {
  constructor(
    public answer: string) { }
}

export class AssignmentDto {
  constructor(
    public hint: string,
    public variablesCount: number,
    public status: AssignmentStatus,
    public restrictionType: AssignmentRestrictionType,
    public attemptsRemaining: number,
    public deadline: string,
    public nextAttemptTime: string
  ) { }
}
export class AssignmentResponseDto {
  constructor(
    public result: number,
    public hasCorrectAnswer: boolean,
    public restrictionType: AssignmentRestrictionType,
    public attemptsRemaining: number,
    public deadline: string,
    public nextAttemptTime: string
  ) { }
}
export class UserAssignmentDto {
  constructor(
    public id: number,
    public hint: string,
    public status: AssignmentStatus,
    public functionResultType: FunctionResultType,
    public restrictionType: AssignmentRestrictionType,
    public attemptsRemaining: number,
    public deadline: string,
    public nextAttemptTime: string,
    public mark: number,
    public comment: string,
    public user: UserDto
  ) { }
}

export class AssignDto {
  constructor(public subjectIds: number[]) { }
}

export class AnswerDto {
  constructor(public numberOfAnswer: number, public answer: string, public result: number, public isCorrect: boolean) { }
}
