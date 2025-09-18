export function parseToNumber(input: string | string[]): number {
  if (Array.isArray(input)) {
    return Number(input[0]);
  }
  return Number(input);
}
