import { ValidatorFn, AbstractControl, ValidationErrors } from "@angular/forms";

export function booleanValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value as string;
    if (value.length > 0 && value.toLowerCase() !== 'true' && value .toLowerCase() !== 'false') {
      return { isBoolean: true };
    }
    return null;
  };
}
