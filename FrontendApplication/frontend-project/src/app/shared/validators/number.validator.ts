import { ValidatorFn, AbstractControl, ValidationErrors } from "@angular/forms";

export function integerValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value as string;
    if (value.length > 0 && /^[0-9]+$/.test(value) == false) {
      return { isInteger: true };
    }
    return null;
  };
}

export function decimalValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value as string;
    if (value.length > 0 && /^-?\d+(\.\d+)?$/.test(value) == false) {
      return { isDecimal: true };
    }
    return null;
  };
}