export class MarkAssignmentDto {
  constructor(
    public markId: number,
    public userId: number,
    public mark: number,
    public comment: string
  ) { }
}

export class AssignmentsToMarkDto {
  constructor(
  ) { }
}
