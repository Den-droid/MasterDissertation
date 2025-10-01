import { ValidatorFn, AbstractControl, ValidationErrors } from "@angular/forms";

export function notAuthUrlValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value as string;
    if (value.length > 0 && value.toLowerCase().includes('/api/auth')) {
      return { isAuthUrl: true };
    }
    return null;
  };
}
