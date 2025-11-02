export class UniversityDto {
    public constructor(public id: number, public name: string) { }
}

export class AddUniversityDto {
    public constructor(public name: string) {
    }
}

export class UpdateUniversityDto {
    public constructor(public name: string) {
    }
}