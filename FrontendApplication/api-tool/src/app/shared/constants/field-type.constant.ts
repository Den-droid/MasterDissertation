export enum FieldType {
  INTEGER, DECIMAL, STRING, BOOLEAN, DATETIME, ENUM
}

export enum FieldTypeLabel {
  INTEGER = "int32",
  DECIMAL = "float",
  STRING = "varchar",
  BOOLEAN = 'boolean',
  DATETIME = 'datetime',
  ENUM = 'int16'
}

export function mapFieldTypeToLabel(fieldType: FieldType) {
  const key = FieldType[fieldType] as keyof typeof FieldTypeLabel;
  return FieldTypeLabel[key];
}
