import { AssignmentRestrictionType } from "../constants/assignment-restriction-type";
import { AssignmentStatus } from "../constants/assignment-status.constant";
import { FunctionDto } from "./function.model";
import { MarkDto } from "./mark.model";
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
    public isWall: boolean,
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
    public restrictionType: AssignmentRestrictionType,
    public attemptsRemaining: number,
    public deadline: string,
    public nextAttemptTime: string,
    public mark: MarkDto,
    public user: UserDto
  ) { }
}

export class UserAssignmentWithFunctionDto {
  constructor(
    public id: number,
    public hint: string,
    public status: AssignmentStatus,
    public restrictionType: AssignmentRestrictionType,
    public attemptsRemaining: number,
    public deadline: string,
    public nextAttemptTime: string,
    public mark: MarkDto,
    public user: UserDto,
    public func: FunctionDto | null
  ) { }
}

export class AssignmentFunctionDto {
  public constructor(public functionDto: FunctionDto, public userAssignmentId: number) { }
}

export class AssignFunctionDto {
  constructor(public subjectIds: number[]) { }
}

export class AssignToGroupDto {
  constructor(public groupId: number) { }
}

export class AnswerDto {
  constructor(public numberOfAnswer: number, public answer: string, public result: number, public isCorrect: boolean) { }
}

export function mapToUserAssignmentWithFunctionDto(userAssignmentDto: UserAssignmentDto) {
  return new UserAssignmentWithFunctionDto(userAssignmentDto.id, userAssignmentDto.hint,
    userAssignmentDto.status, userAssignmentDto.restrictionType,
    userAssignmentDto.attemptsRemaining, userAssignmentDto.deadline, userAssignmentDto.nextAttemptTime,
    userAssignmentDto.mark, userAssignmentDto.user, null
  );
}
