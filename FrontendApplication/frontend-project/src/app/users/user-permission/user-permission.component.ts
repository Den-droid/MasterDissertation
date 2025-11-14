import { CommonModule, Location } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { debounceTime, Subject } from "rxjs";
import { UserAssignmentDto } from "../../shared/models/assignment.model";
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
import { userPermissionsLabels, userValidation } from "../../shared/translations/permission.translation";
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

    subjects: SubjectDto[] = [];
    filteredSubjects: SubjectDto[] = [];
    selectedSubjects: number[] = [];

    functions: FunctionDto[] = [];
    filteredFunctions: FunctionDto[] = [];
    selectedFunctions: number[] = [];

    assignments: UserAssignmentDto[] = [];
    filteredAssignments: UserAssignmentDto[] = [];
    selectedAssignments: number[] = [];

    searchUniversities: string = '';
    searchUniversitiesSubject: Subject<string> = new Subject<string>();

    searchSubjects: string = '';
    searchSubjectsSubject: Subject<string> = new Subject<string>();

    searchFunctions: string = '';
    searchFunctionsSubject: Subject<string> = new Subject<string>();

    searchAssignments: string = '';
    searchAssignmentsSubject: Subject<string> = new Subject<string>();

    form!: FormGroup;
    userId!: number;
    saveButtonClicked: boolean = false;
    showError: boolean = false;
    errorMessage = '';

    atLeastOneMessage = userValidation["at-least-one"];
    includeSubjectText = userPermissionsLabels["include-subjects"];
    includeFunctionText = userPermissionsLabels["include-functions"];
    title = usersPageTitles["user-permissions"];

    constructor(private location: Location, private fb: FormBuilder, private route: ActivatedRoute,
        private readonly router: Router, private universityService: UniversityService,
        private subjectService: SubjectService, private functionService: FunctionService,
        private assignmentService: AssignmentService, private permissionService: PermissionService
    ) { }

    ngOnInit(): void {
        this.getUniversities();
        this.getSubjects();
        this.getFunctions();
        this.getAssignments();
        this.getUserPermissions();

        this.route.paramMap.subscribe(params => {
            this.userId = Number(params.get('id'));
        });

        this.form = this.fb.group({
            universitySearch: [''],
            subjectSearch: [''],
            functionSearch: [''],
            assignmentSearch: [''],
            includeSubjects: [false],
            includeFunctions: [false],
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
            );
        });

        this.searchFunctionsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredFunctions = this.functions.filter(f =>
                f.text.toLowerCase().includes(value.toLowerCase())
            );
        });

        this.searchAssignmentsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredAssignments = this.assignments.filter(a =>
                this.getStudentFullName(a.user).toLowerCase().includes(value.toLowerCase())
            );
        });
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
                this.assignments = dto;
                this.filteredAssignments = [...this.assignments];
            }
        })
    }

    getUserPermissions() {
        this.permissionService.getByUserId(this.userId).subscribe({
            next: (dto: PermissionDto[]) => {
                for (let userPermission of dto) {
                    if (userPermission.universityId != null) {
                        this.selectedUniversities.push(userPermission.universityId)
                    } else if (userPermission.subjectId != null) {
                        this.selectedSubjects.push(userPermission.subjectId)
                    } else if (userPermission.functionId != null) {
                        this.selectedFunctions.push(userPermission.functionId)
                    } else if (userPermission.userAssignmentId != null) {
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
            if (this.selectedUniversities.length === 0) {
                this.filteredSubjects = [...this.subjects];
                this.filteredFunctions = [...this.functions];
                this.filteredAssignments = [...this.assignments];
            } else {
                this.filteredSubjects = this.subjects.filter(s => this.selectedUniversities.includes(s.university.id));
                this.filteredFunctions = this.functions.filter(f => this.filteredSubjects
                    .map(fs => fs.id)
                    .includes(f.subject.id));
                // this.filteredAssignments = this.assignments.filter(a => this.filteredFunctions
                //     .map(ff => ff.id)
                //     .includes(a.)
                // )
            }
        } else {
            this.selectedUniversities.push(id);
            this.filteredSubjects = this.subjects.filter(s => this.selectedUniversities.includes(s.university.id));
            this.filteredFunctions = this.functions.filter(f => this.filteredSubjects
                .map(fs => fs.id)
                .includes(f.subject.id));
            // this.filteredAssignments = this.assignments.filter(a => this.filteredFunctions
            //     .map(ff => ff.id)
            //     .includes(a.)
            // )
        }

        this.onSubjectsSearchChange(this.searchSubjects);
        this.onFunctionsSearchChange(this.searchFunctions);
        this.onAssignmentsSearchChange(this.searchAssignments);
    }

    toggleSubject(id: number) {
        if (this.selectedSubjects.includes(id)) {
            this.selectedSubjects = this.selectedSubjects.filter(ss => ss !== id);
            if (this.selectedSubjects.length === 0) {
                this.filteredFunctions = [...this.functions];
                this.filteredAssignments = [...this.assignments];
            } else {
                this.filteredFunctions = this.functions.filter(f => this.selectedSubjects.includes(f.subject.id));
                // this.filteredAssignments = this.assignments.filter(a => this.filteredFunctions
                //     .map(ff => ff.id)
                //     .includes(a.)
                // )
            }
        } else {
            this.selectedSubjects.push(id);
            this.filteredFunctions = this.functions.filter(f => this.selectedSubjects.includes(f.subject.id));
            // this.filteredAssignments = this.assignments.filter(a => this.filteredFunctions
            //     .map(ff => ff.id)
            //     .includes(a.)
            // )
        }

        this.onFunctionsSearchChange(this.searchFunctions);
        this.onAssignmentsSearchChange(this.searchAssignments);
    }

    toggleFunction(id: number) {
        if (this.selectedFunctions.includes(id)) {
            this.selectedFunctions = this.selectedFunctions.filter(sf => sf !== id);
            if (this.selectedFunctions.length === 0) {
                this.filteredAssignments = [...this.assignments];
            } else {
                // this.filteredAssignments = this.assignments.filter(a => this.filteredFunctions
                //     .map(ff => ff.id)
                //     .includes(a.)
                // )
            }
        } else {
            this.selectedFunctions.push(id);
            // this.filteredAssignments = this.assignments.filter(a => this.filteredFunctions
            //     .map(ff => ff.id)
            //     .includes(a.)
            // )
        }

        this.onAssignmentsSearchChange(this.searchAssignments);
    }

    toggleAssignment(id: number) {
        if (this.selectedAssignments.includes(id)) {
            this.selectedAssignments = this.selectedAssignments.filter(sa => sa !== id);
        } else {
            this.selectedAssignments.push(id);
        }
    }

    save() {
        this.saveButtonClicked = true;
        this.validate();

        if (this.form.invalid) {
            return;
        }

        if (this.nothingSelected()) {
            return;
        }

        let subjectsToSend: number[] = [], functionsToSend: number[] = [];

        if (this.form.value.includeSubjects) {
            this.selectedSubjects.forEach(ss => subjectsToSend.push(ss));
        }

        if (this.form.value.includeFunctions) {
            this.selectedFunctions.forEach(sf => functionsToSend.push(sf));
        }

        let dto = new UpdatePermissionsDto(this.userId, [], subjectsToSend,
            functionsToSend, this.selectedAssignments);

        this.permissionService.updatePermissions(dto).subscribe({
            complete: () => {
                this.router.navigate([`/users`]);
            }
        })
    }

    goBack() {
        this.location.back();
    }

    validate() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
    }

    nothingSelected() {
        return this.selectedSubjects.length === 0
            && this.selectedFunctions.length === 0 && this.selectedAssignments.length === 0;
    }
}