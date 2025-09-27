import { AssignmentRestrictionType } from "../constants/assignment-restriction-type";
import { AssignmentStatus } from "../constants/assignment-status.constant";
import { FunctionResultType } from "../constants/function-result-type.constant";

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
    public userAssignmentId: number,
    public hint: string,
    public status: AssignmentStatus,
    public functionResultType: FunctionResultType,
    public restrictionType: AssignmentRestrictionType,
    public attemptsRemaining: number,
    public deadline: string,
    public nextAttemptTime: string,
    public mark: number,
    public comment: string
  ) { }
}

export class UserAssignment {
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
  ) {

  }
}

export function parseUserAssignmentDtoToAssignment(userAssignmentDto: UserAssignmentDto): UserAssignment {
  return new UserAssignment(userAssignmentDto.userAssignmentId, userAssignmentDto.hint,
    userAssignmentDto.status, userAssignmentDto.functionResultType, userAssignmentDto.restrictionType,
    userAssignmentDto.attemptsRemaining, userAssignmentDto.deadline,
    userAssignmentDto.nextAttemptTime, userAssignmentDto.mark, userAssignmentDto.comment
  );
}
