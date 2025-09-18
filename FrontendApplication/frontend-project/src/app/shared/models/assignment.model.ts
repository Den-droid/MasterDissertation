import { AssignmentStatus } from "../constants/assignment-status.constant";
import { FunctionResultType } from "../constants/function-result-type.constant";

export class AssignmentAnswerDto {
  constructor(
    public answer: string) { }
}

export class AssignmentDto {
  constructor(
    public hint: string,
    public attemptRemaining: number,
    public variablesCount: number
  ) { }
}

export class AssignmentResponseDto {
  constructor(
    public result: number,
    public attemptsRemaining: number,
    public hasCorrectAnswer: boolean
  ) { }
}
export class UserAssignmentDto {
  constructor(
    public assignmentId: number,
    public hint: string,
    public attemptsRemaining: number,
    public statusId: number,
    public functionResultTypeId: number,
    public mark: number,
    public comment: string
  ) { }
}

export class Assignment {
  constructor(
    public id: number,
    public hint: string,
    public attemptsRemaining: number,
    public status: AssignmentStatus,
    public functionResultType: FunctionResultType,
    public mark: number,
    public comment: string,
  ) {

  }
}

export function parseUserAssignmentDtoToAssignment(userAssignmentDto: UserAssignmentDto): Assignment {
  return new Assignment(userAssignmentDto.assignmentId, userAssignmentDto.hint,
    userAssignmentDto.attemptsRemaining, userAssignmentDto.statusId,
    userAssignmentDto.functionResultTypeId, userAssignmentDto.mark, userAssignmentDto.comment
  );
}
