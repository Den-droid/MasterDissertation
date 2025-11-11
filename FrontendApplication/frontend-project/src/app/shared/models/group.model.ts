export class GroupDto {
    constructor(public id: number, public name: string,
        public students: GroupStudentDto[], public subjects: GroupSubjectDto[]) { }
}

export class AddGroupDto {
    constructor(public name: string) { }
}

export class UpdateGroupDto {
    constructor(public name: string) { }
}

export class GroupStudentDto {
    constructor(public id: number, public firstName: string, public lastName: string) { }
}

export class GroupSubjectDto {
    constructor(public id: number, public name: string) { }
}