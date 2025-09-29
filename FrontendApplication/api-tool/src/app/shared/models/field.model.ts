import { FieldType } from "../constants/field-type.constant";

export class FieldDto {
  constructor(
    public id: number,
    public name: string,
    public label: string,
    public description: string,
    public type: FieldType,
    public required: boolean
  ) {
  }
}
