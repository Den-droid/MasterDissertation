import { CommonModule, Location } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { debounceTime, Subject } from "rxjs";
import { AssignmentType, AssignmentTypeLabel } from "../../shared/constants/assignment-type";
import { RoleLabel, RoleName } from "../../shared/constants/roles.constant";
import { InfoModalComponent } from "../../shared/modals/info/info-modal.component";
import { AssignmentFunctionDto, UserAssignmentDto, UserAssignmentWithFunctionDto } from "../../shared/models/assignment.model";
import { FunctionDto } from "../../shared/models/function.model";
import { PermissionDto, UpdatePermissionsDto } from "../../shared/models/permission.model";
import { SubjectDto } from "../../shared/models/subject.model";
import { UniversityDto } from "../../shared/models/university.model";
import { UserDto } from "../../shared/models/user.model";
import { AssignmentService } from "../../shared/services/assignment.service";
import { FunctionService } from "../../shared/services/function.service";
import { PermissionService } from "../../shared/services/permission.service";
import { SubjectService } from "../../shared/services/subject.service";
import { UniversityService } from "../../shared/services/university.service";
import { UserService } from "../../shared/services/user.service";
import { modalText } from "../../shared/translations/common.translation";
import { userPermissionsLabels, userPermissionsModalText, userValidation } from "../../shared/translations/permission.translation";
import { usersPageTitles } from "../../shared/translations/user.translation";

@Component({
    selector: 'app-user-permission',
    templateUrl: './user-permission.component.html',
    imports: [FormsModule, ReactiveFormsModule, CommonModule]
})
export class UserPermissionComponent implements OnInit {
    universities: UniversityDto[] = [];
    filteredUniversities: UniversityDto[] = [];
    selectedUniversities: number[] = [];
    originalSelectedUniversities: number[] = [];

    subjects: SubjectDto[] = [];
    filteredSubjects: SubjectDto[] = [];
    selectedSubjects: number[] = [];
    originalSelectedSubjects: number[] = [];

    functions: FunctionDto[] = [];
    filteredFunctions: FunctionDto[] = [];
    selectedFunctions: number[] = [];
    originalSelectedFunctions: number[] = [];

    assignments: UserAssignmentWithFunctionDto[] = [];
    filteredAssignments: UserAssignmentWithFunctionDto[] = [];
    selectedAssignments: number[] = [];
    originalSelectedAssignments: number[] = [];

    searchUniversitiesSubject: Subject<string> = new Subject<string>();
    searchSubjectsSubject: Subject<string> = new Subject<string>();
    searchFunctionsSubject: Subject<string> = new Subject<string>();
    searchAssignmentsSubject: Subject<string> = new Subject<string>();

    user!: UserDto;

    form!: FormGroup;
    userId!: number;
    saveButtonClicked: boolean = false;
    showError: boolean = false;
    errorMessage = '';

    atLeastOneMessage = userValidation["at-least-one"];
    atLeastOneWithUniversityMessage = userValidation["at-least-one-with-university"];
    includeUniversitiesText = userPermissionsLabels["include-universities"];
    includeSubjectText = userPermissionsLabels["include-subjects"];
    includeFunctionText = userPermissionsLabels["include-functions"];
    title = usersPageTitles["user-permissions"];

    constructor(private location: Location, private fb: FormBuilder, private route: ActivatedRoute,
        private readonly router: Router, private universityService: UniversityService,
        private subjectService: SubjectService, private functionService: FunctionService,
        private assignmentService: AssignmentService, private permissionService: PermissionService,
        private userService: UserService, private modalService: NgbModal
    ) { }

    ngOnInit(): void {
        this.getUniversities();
        this.getSubjects();
        this.getFunctions();
        this.getAssignments();

        this.route.paramMap.subscribe(params => {
            this.userId = Number(params.get('id'));
            this.getUser();
            this.getUserPermissions();
        });

        this.form = this.fb.group({
            universitySearch: [''],
            subjectSearch: [''],
            functionSearch: [''],
            assignmentSearch: [''],
            includeUniversities: [false],
            includeSubjects: [false],
            includeFunctions: [false]
        });

        this.form.get('universitySearch')?.valueChanges.subscribe({
            next: (value) => {
                this.onUniversitiesSearchChange(value);
            }
        })

        this.form.get('subjectSearch')?.valueChanges.subscribe({
            next: (value) => {
                this.onSubjectsSearchChange(value);
            }
        })

        this.form.get('functionSearch')?.valueChanges.subscribe({
            next: (value) => {
                this.onFunctionsSearchChange(value);
            }
        })

        this.form.get('assignmentSearch')?.valueChanges.subscribe({
            next: (value) => {
                this.onAssignmentsSearchChange(value);
            }
        })

        this.searchUniversitiesSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredUniversities = this.universities.filter(u =>
                u.name.toLowerCase().includes(value.toLowerCase())
            );
        });

        this.searchSubjectsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredSubjects = this.subjects.filter(s =>
                s.name.toLowerCase().includes(value.toLowerCase())
            ).filter(s => this.selectedUniversities.includes(s.university.id) || this.selectedUniversities.length === 0);
        });

        this.searchFunctionsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredFunctions = this.functions.filter(f =>
                f.text.toLowerCase().includes(value.toLowerCase())
            ).filter(f => this.selectedSubjects.includes(f.subject.id) || this.selectedSubjects.length === 0);
        });

        this.searchAssignmentsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredAssignments = this.assignments.filter(a =>
                (this.getStudentFullName(a.user) + ' (' + this.getAssignmentTypeString(a) + ')')
                    .toLowerCase().includes(value.toLowerCase())
            ).filter(a => (a.func != null ?
                this.selectedFunctions.includes(a.func.id) : false) || this.selectedFunctions.length === 0
            );
        });
    }

    getUser() {
        this.userService.getByUserId(this.userId).subscribe({
            next: (dto: UserDto) => {
                this.user = dto;
            }
        });
    }

    isUserAdmin() {
        return this.getRoleLabel(this.user.role) == RoleLabel.ADMIN;
    }

    getRoleLabel(roleName: string) {
        const key = Object.keys(RoleName).find(
            k => RoleName[k as keyof typeof RoleName] === roleName
        );
        return RoleLabel[key as keyof typeof RoleLabel];
    }

    getUniversities() {
        this.universityService.getAll().subscribe({
            next: (dto: UniversityDto[]) => {
                this.universities = dto;
                this.filteredUniversities = [...this.universities];
            }
        })
    }

    getSubjects() {
        this.subjectService.getAll().subscribe({
            next: (dto: SubjectDto[]) => {
                this.subjects = dto;
                this.filteredSubjects = [...this.subjects];
            }
        })
    }

    getFunctions() {
        this.functionService.getAll().subscribe({
            next: (dto: FunctionDto[]) => {
                this.functions = dto;
                this.filteredFunctions = [...this.functions];
            }
        })
    }

    getAssignments() {
        this.assignmentService.get().subscribe({
            next: (dto: UserAssignmentDto[]) => {
                this.functionService.getByAssignmentIds(dto.map(ua => ua.id)).subscribe({
                    next: (assignmentFunctions: AssignmentFunctionDto[]) => {
                        let getFunction = (userAssignment: UserAssignmentDto) => {
                            let functionDto = assignmentFunctions
                                .find(af => af.userAssignmentId == userAssignment.id)?.functionDto;
                            return functionDto ? functionDto : null;
                        };
                        this.assignments = dto.map(ua => new UserAssignmentWithFunctionDto(
                            ua.id, ua.hint, ua.status, ua.assignmentType, ua.restrictionType, ua.attemptsRemaining,
                            ua.deadline, ua.minutesToDo, ua.mark, ua.user, getFunction(ua))
                        );
                        this.filteredAssignments = [...this.assignments];
                    }
                })
            }
        })
    }

    getUserPermissions() {
        this.permissionService.getByUserId(this.userId).subscribe({
            next: (dto: PermissionDto[]) => {
                for (let userPermission of dto) {
                    if (userPermission.universityId != null) {
                        this.originalSelectedUniversities.push(userPermission.universityId);
                        this.selectedUniversities.push(userPermission.universityId)
                    } else if (userPermission.subjectId != null) {
                        this.originalSelectedSubjects.push(userPermission.subjectId);
                        this.selectedSubjects.push(userPermission.subjectId)
                    } else if (userPermission.functionId != null) {
                        this.originalSelectedFunctions.push(userPermission.functionId);
                        this.selectedFunctions.push(userPermission.functionId)
                    } else if (userPermission.userAssignmentId != null) {
                        this.originalSelectedAssignments.push(userPermission.userAssignmentId);
                        this.selectedAssignments.push(userPermission.userAssignmentId)
                    }
                }
            }
        })
    }

    get f() {
        return this.form.controls;
    }

    getStudentFullName(student: UserDto) {
        return student.firstName + " " + student.lastName;
    }

    getAssignmentTypeString(assignment: UserAssignmentWithFunctionDto) {
        let key = AssignmentType[assignment.assignmentType.type] as keyof typeof AssignmentTypeLabel;
        return AssignmentTypeLabel[key];
    }

    onUniversitiesSearchChange(value: string) {
        this.searchUniversitiesSubject.next(value);
    }

    onSubjectsSearchChange(value: string) {
        this.searchSubjectsSubject.next(value);
    }

    onFunctionsSearchChange(value: string) {
        this.searchFunctionsSubject.next(value);
    }

    onAssignmentsSearchChange(value: string) {
        this.searchAssignmentsSubject.next(value);
    }

    isUniversitySelected(id: number) {
        return this.selectedUniversities.includes(id);
    }

    isSubjectSelected(id: number) {
        return this.selectedSubjects.includes(id);
    }

    isFunctionSelected(id: number) {
        return this.selectedFunctions.includes(id);
    }

    isAssignmentSelected(id: number) {
        return this.selectedAssignments.includes(id);
    }

    toggleUniversity(id: number) {
        if (this.selectedUniversities.includes(id)) {
            this.selectedUniversities = this.selectedUniversities.filter(su => su !== id);
        } else {
            this.selectedUniversities.push(id);
        }

        this.filteredSubjects = this.subjects.filter(s =>
            s.name.toLowerCase().includes(this.form.value.subjectSearch.toLowerCase())
        );

        this.filteredFunctions = this.functions.filter(f =>
            f.text.toLowerCase().includes(this.form.value.functionSearch.toLowerCase())
        );

        this.filteredAssignments = this.assignments.filter(a =>
            (this.getStudentFullName(a.user) + ' (' + this.getAssignmentTypeString(a) + ')')
                .toLowerCase().includes(this.form.value.assignmentSearch.toLowerCase())
        );

        if (this.selectedUniversities.length > 0) {
            this.filteredSubjects = this.filteredSubjects.filter(s => this.selectedUniversities.includes(s.university.id));
            this.filteredFunctions = this.filteredFunctions.filter(f => this.filteredSubjects
                .map(fs => fs.id)
                .includes(f.subject.id));
            this.filteredAssignments = this.filteredAssignments
                .filter(a => a.func != null ? this.filteredFunctions
                    .map(ff => ff.id)
                    .includes(a.func.id) : false
                )
        }
    }

    toggleSubject(id: number) {
        if (this.selectedSubjects.includes(id)) {
            this.selectedSubjects = this.selectedSubjects.filter(ss => ss !== id);
        } else {
            this.selectedSubjects.push(id);
        }

        this.filteredFunctions = this.functions.filter(f =>
            f.text.toLowerCase().includes(this.form.value.functionSearch.toLowerCase())
        );

        this.filteredAssignments = this.assignments.filter(a =>
            (this.getStudentFullName(a.user) + ' (' + this.getAssignmentTypeString(a) + ')')
                .toLowerCase().includes(this.form.value.assignmentSearch.toLowerCase())
        );

        if (this.selectedSubjects.length > 0) {
            this.filteredFunctions = this.filteredFunctions.filter(f => this.selectedSubjects.includes(f.subject.id));
            this.filteredAssignments = this.filteredAssignments
                .filter(a => a.func != null ? this.filteredFunctions
                    .map(ff => ff.id)
                    .includes(a.func.id) : false
                )
        }
    }

    toggleFunction(id: number) {
        if (this.selectedFunctions.includes(id)) {
            this.selectedFunctions = this.selectedFunctions.filter(sf => sf !== id);
        } else {
            this.selectedFunctions.push(id);
        }

        this.filteredAssignments = this.assignments.filter(a =>
            (this.getStudentFullName(a.user) + ' (' + this.getAssignmentTypeString(a) + ')')
                .toLowerCase().includes(this.form.value.assignmentSearch.toLowerCase())
        );

        if (this.selectedFunctions.length > 0) {
            this.filteredAssignments = this.filteredAssignments.filter(a =>
                a.func != null ?
                    this.selectedFunctions.includes(a.func.id) : false);
        }
    }

    toggleAssignment(id: number) {
        if (this.selectedAssignments.includes(id)) {
            this.selectedAssignments = this.selectedAssignments.filter(sa => sa !== id);
        } else {
            this.selectedAssignments.push(id);
        }
    }

    validate() {
        this.saveButtonClicked = true;

        if (this.nothingSelected()) {
            return false;
        }

        return true;
    }

    getUpdatePermissionsDto() {
        let universitiesToSend: number[] = [], subjectsToSend: number[] = [], functionsToSend: number[] = [];

        if (this.form.value.includeUniversities) {
            this.selectedUniversities.forEach(su => universitiesToSend.push(su));
        }

        if (this.form.value.includeSubjects) {
            this.selectedSubjects.forEach(ss => subjectsToSend.push(ss));
        }

        if (this.form.value.includeFunctions) {
            this.selectedFunctions.forEach(sf => functionsToSend.push(sf));
        }

        return new UpdatePermissionsDto(this.userId, universitiesToSend, subjectsToSend,
            functionsToSend, this.selectedAssignments, []);
    }

    revertChanges() {
        this.saveButtonClicked = false;
        this.selectedUniversities = [...this.originalSelectedUniversities];
        this.selectedSubjects = [...this.originalSelectedSubjects];
        this.selectedFunctions = [...this.originalSelectedFunctions];
        this.selectedAssignments = [...this.originalSelectedAssignments];
    }

    save() {
        if (!this.validate()) {
            return;
        }
        this.saveOriginalValues();
        let dto = this.getUpdatePermissionsDto();

        this.permissionService.updatePermissions(dto).subscribe({
            complete: () => {
                const modalRef = this.modalService.open(InfoModalComponent, {
                    centered: true,
                    backdrop: true,
                    keyboard: false
                });

                modalRef.componentInstance.content = [userPermissionsModalText["successful-update"]];
                modalRef.componentInstance.title = modalText['success-header'];
            }
        })
    }

    saveOriginalValues() {
        this.originalSelectedUniversities = [...this.selectedUniversities];
        this.originalSelectedSubjects = [...this.selectedSubjects];
        this.originalSelectedFunctions = [...this.selectedFunctions];
        this.originalSelectedAssignments = [...this.selectedAssignments];
    }

    saveAndClose() {
        if (!this.validate()) {
            return;
        }
        this.saveOriginalValues();
        let dto = this.getUpdatePermissionsDto();

        this.permissionService.updatePermissions(dto).subscribe({
            complete: () => {
                this.router.navigate([`/users`]);
            }
        })
    }

    goBack() {
        this.location.back();
    }

    nothingSelected() {
        return (this.form.value.includeUniversities ? this.selectedUniversities.length === 0 : true)
            && (this.form.value.includeSubjects ? this.selectedSubjects.length === 0 : true)
            && (this.form.value.includeFunctions ? this.selectedFunctions.length === 0 : true)
            && this.selectedAssignments.length === 0;
    }
}