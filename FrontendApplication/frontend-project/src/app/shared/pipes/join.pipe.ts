import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: 'join'
})
export class JoinPipe implements PipeTransform {
    transform(value: any[], separator: string = ', '): string {
        let str = Array.isArray(value) ? value.join(separator) : '';
        return str;
    }
}