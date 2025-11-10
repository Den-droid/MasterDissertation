export class MarkDto {
  constructor(
    public id: number | null,
    public mark: number,
    public comment: string
  ) { }
}

export class MarkModalDto {
  constructor(
    public mark: number,
    public comment: string
  ) { }
}