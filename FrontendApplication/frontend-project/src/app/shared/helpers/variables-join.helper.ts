export function joinVariables(variablesValues: string[], variablesNames: string[]) : string {
  let res = '';
  for (let i = 0; i < variablesNames.length; i++) {
    res += variablesNames[i];
    res += '=';
    res += variablesValues[i];
    res += ';';
  }
  return res;
}
