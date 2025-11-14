export class PermissionDto {
    constructor(public id: number, public userId: number, public universityId: number | null,
        public subjectId: number | null, public functionId: number | null, public userAssignmentId: number | null
    ) { }
}

export class UpdatePermissionsDto {
    constructor(public userId: number, public universityIds: number[],
        public subjectIds: number[], public functionId: number[], public userAssignmentId: number[]
    ) { }
}