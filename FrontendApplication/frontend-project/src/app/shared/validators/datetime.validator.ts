import { ValidatorFn, AbstractControl, ValidationErrors } from "@angular/forms";

export function datetimeValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
        const value = control.value as string;
        const date = new Date(value);
        if (value.length > 0 && isNaN(date.getTime())) {
            return { isDatetime: true };
        }
        return null;
    };
}