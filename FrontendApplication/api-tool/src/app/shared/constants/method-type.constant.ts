export enum MethodType {
  GET, POST, PUT, DELETE
}

export enum MethodTypeLabel {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  DELETE = "DELETE"
}

export function mapMethodTypeToLabel(methodType : MethodType) {
  const key = MethodType[methodType] as keyof typeof MethodTypeLabel;
  return MethodTypeLabel[key];
}
