export class AssignmentAnswerDto {
  constructor(
    public answer: string)
    { }
}

export class AssignmentDto {
  constructor(
    public hint: string,
    public attemptRemaining: number,
    public variablesCount: number
  ) {}
}

export class AssignmentResponseDto {
  constructor(
    public result: number,
    public attemptsRemaining: number,
    public hasCorrectAnswer: boolean
  ) {}
}

export class UserAssignmentDto {
  constructor(
    public statusId: number,
    public functionResultTypeId: number,
    public lastAnswer: string,
    public mark: number,
    public comment: string,
    public lastAnswerCorrect: boolean
  ) {}
}
