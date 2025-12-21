export class UrlDto {
  constructor(
    public id: number,
    public url: string,
    public description: string,
    public method: MethodTypeDto
  ) { }
}

export class MethodTypeDto {
  constructor(
    public method: number,
    public name: string
  ) { }
}
