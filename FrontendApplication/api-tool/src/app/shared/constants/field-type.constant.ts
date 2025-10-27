export enum FieldType {
  INTEGER, DECIMAL, STRING, DATETIME, ENUM
}

export enum FieldTypeLabel {
  INTEGER = "int32",
  DECIMAL = "float",
  STRING = "varchar",
  DATETIME = 'datetime',
  ENUM = 'int16'
}

export function mapFieldTypeToLabel(fieldType: FieldType) {
  const key = FieldType[fieldType] as keyof typeof FieldTypeLabel;
  return FieldTypeLabel[key];
}
