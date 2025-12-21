import { AssignmentRestrictionType } from "../constants/assignment-restriction-type";

export class ModalRestrictionDto {
    constructor(public restrictionType: AssignmentRestrictionType, public attemptsRemaining: number | null,
        public minutesToDo: number | null, public deadline: string | null
    ) { }
}

export class RestrictionDto {
    constructor(public restrictionType: AssignmentRestrictionType, public universityId: number | null,
        public subjectId: number | null, public functionId: number | null, public userAssignmentId: number | null,
        public mazeId: number | null,
        public attemptsRemaining: number | null,
        public minutesToDo: number | null, public deadline: string | null
    ) { }
}

export class ReadableRestrictionDto {
    constructor(public restrictionType: RestrictionTypeDto, public universityId: number | null,
        public subjectId: number | null, public functionId: number | null, public userAssignmentId: number | null,
        public mazeId: number | null,
        public attemptsRemaining: number | null,
        public minutesToDo: number | null, public deadline: string | null
    ) { }
}

export class DefaultRestrictionDto {
    constructor(public id: number | null, public restrictionType: AssignmentRestrictionType, public universityId: number | null,
        public subjectId: number | null, public functionId: number | null, public mazeId: number | null,
        public attemptsRemaining: number | null,
        public minutesToDo: number | null, public deadline: string | null
    ) { }
}

export class ReadableDefaultRestrictionDto {
    constructor(public id: number | null, public restrictionType: RestrictionTypeDto, public universityId: number | null,
        public subjectId: number | null, public functionId: number | null, public mazeId: number | null,
        public attemptsRemaining: number | null,
        public minutesToDo: number | null, public deadline: string | null
    ) { }
}

export class RestrictionTypeDto {
    constructor(public type: number, public name: string) {
    }
}