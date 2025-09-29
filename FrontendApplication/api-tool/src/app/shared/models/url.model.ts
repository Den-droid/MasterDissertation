import { MethodType } from "../constants/method-type.constant";

export class UrlDto {
  constructor(
    public id: number,
    public url: string,
    public description: string,
    public method: MethodType
  ) { }
}

export class MethodTypeDto {
  constructor(
    public method: number,
    public label: string
  ) { }
}
