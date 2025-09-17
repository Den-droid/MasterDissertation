export function joinVariables(variablesValues: string[], variablesNames: string[]): string {
  let res = '';
  for (let i = 0; i < variablesNames.length; i++) {
    res += variablesNames[i];
    res += '=';
    res += variablesValues[i];
    res += ';';
  }
  return res;
}

export function splitVariables(answer: string): Map<string, string> {
  let answerParts = answer.split(';=');
  let map = new Map<string, string>();
  for (let i = 0; i < answerParts.length / 2; i++) {
    map.set(answerParts[i * 2], answerParts[i * 2 + 1])
  }
  return map;
}
