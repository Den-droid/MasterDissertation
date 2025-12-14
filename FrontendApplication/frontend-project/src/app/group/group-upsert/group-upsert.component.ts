import { CommonModule, Location } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { UserDto } from "../../shared/models/user.model";
import { SubjectDto } from "../../shared/models/subject.model";
import { debounceTime, Subject } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { groupLabels, groupTitles } from "../../shared/translations/group.translation";
import { GroupService } from "../../shared/services/group.service";
import { ActionType } from "../../shared/constants/action-type";
import { AddGroupDto, GroupDto, GroupStudentDto, GroupSubjectDto, UpdateGroupDto } from "../../shared/models/group.model";
import { IdDto } from "../../shared/models/common.model";
import { UserService } from "../../shared/services/user.service";
import { SubjectService } from "../../shared/services/subject.service";
import { validationMessages } from "../../shared/translations/common.translation";

@Component({
    selector: 'app-group-upsert',
    templateUrl: './group-upsert.component.html',
    imports: [FormsModule, ReactiveFormsModule, CommonModule]
})
export class GroupUpsertComponent implements OnInit {
    students: GroupStudentDto[] = [];
    filteredStudents: GroupStudentDto[] = [];
    selectedStudents: number[] = [];
    subjects: GroupSubjectDto[] = [];
    filteredSubjects: GroupSubjectDto[] = [];
    selectedSubjects: number[] = [];

    searchStudents: string = '';
    searchStudentsSubject: Subject<string> = new Subject<string>();

    searchSubjects: string = '';
    searchsSubjectsSubject: Subject<string> = new Subject<string>();

    form!: FormGroup;
    groupId!: number;
    saveButtonClicked: boolean = false;
    showError: boolean = false;
    errorMessage = '';

    groupNameRequiredMessage = validationMessages["name-required"];
    atLeastOneStudentMessage = groupLabels["at-least-one-student"];
    atLeastOneSubjectMessage = groupLabels["at-least-one-subject"];

    addGroupTitle = groupTitles["add-group"];
    updateGroupTitle = groupTitles["update-group"];

    actionType = ActionType.CREATE;

    constructor(private location: Location, private fb: FormBuilder, private route: ActivatedRoute,
        private groupService: GroupService, private readonly router: Router, private userService: UserService,
        private subjectService: SubjectService
    ) { }

    ngOnInit(): void {
        this.getStudents();
        this.getSubjects();

        this.route.paramMap.subscribe(params => {
            if (params.keys.length > 0) {
                this.groupId = Number(params.get('id'));
                this.actionType = ActionType.UPDATE;

                this.groupService.getById(this.groupId).subscribe({
                    next: (groupDto: GroupDto) => {
                        this.form.get('name')?.setValue(groupDto.name);
                        this.selectedStudents = groupDto.students.map(s => s.id);
                        this.selectedSubjects = groupDto.subjects.map(s => s.id);
                    }
                })
            }
        });

        this.form = this.fb.group({
            name: ['', Validators.required],
            studentSearch: [''],
            subjectSearch: ['']
        });

        this.form.get('studentSearch')?.valueChanges.subscribe({
            next: (value) => {
                this.onStudentSearchChange(value);
            }
        })

        this.form.get('subjectSearch')?.valueChanges.subscribe({
            next: (value) => {
                this.onSubjectsSearchChange(value);
            }
        })

        this.searchStudentsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredStudents = this.students.filter(s =>
                this.getStudentFullName(s).toLowerCase().includes(value.toLowerCase())
            );
        });

        this.searchsSubjectsSubject.pipe(
            debounceTime(500)
        ).subscribe(value => {
            this.filteredSubjects = this.subjects.filter(s =>
                s.name.toLowerCase().includes(value.toLowerCase())
            );
        });
    }

    getStudents() {
        this.userService.getStudents().subscribe({
            next: (dto: UserDto[]) => {
                this.students = dto.map(u => new GroupStudentDto(u.id, u.firstName, u.lastName));
                this.filteredStudents = [...this.students];
            }
        })
    }

    getSubjects() {
        this.subjectService.getAll().subscribe({
            next: (dto: SubjectDto[]) => {
                this.subjects = dto.map(s => new GroupSubjectDto(s.id, s.name));
                this.filteredSubjects = [...this.subjects];
            }
        })
    }

    get f() {
        return this.form.controls;
    }

    isCreatingGroup() {
        return this.actionType == ActionType.CREATE;
    }

    getStudentFullName(student: GroupStudentDto) {
        return student.firstName + " " + student.lastName;
    }

    onStudentSearchChange(value: string) {
        this.searchStudentsSubject.next(value);
    }

    onSubjectsSearchChange(value: string) {
        this.searchsSubjectsSubject.next(value);
    }

    isStudentSelected(id: number) {
        return this.selectedStudents.includes(id);
    }

    isSubjectSelected(id: number) {
        return this.selectedSubjects.includes(id);
    }

    toggleStudent(id: number) {
        if (this.selectedStudents.includes(id)) {
            this.selectedStudents = this.selectedStudents.filter(ss => ss !== id);
        } else {
            this.selectedStudents.push(id);
        }
    }

    toggleSubject(id: number) {
        if (this.selectedSubjects.includes(id)) {
            this.selectedSubjects = this.selectedSubjects.filter(ss => ss !== id);
        } else {
            this.selectedSubjects.push(id);
        }
    }

    save() {
        this.saveButtonClicked = true;
        this.validate();

        if (this.form.invalid) {
            return;
        }

        let anyStudentSelected = this.selectedStudents.length > 0;
        let anySubjectSelected = this.selectedSubjects.length > 0;

        if (!anyStudentSelected || !anySubjectSelected) {
            return;
        }

        if (this.actionType == ActionType.CREATE) {
            let dto = new AddGroupDto(this.form.value.name, this.selectedStudents, this.selectedSubjects);
            this.groupService.add(dto).subscribe({
                error: (error: any) => {
                    this.showError = true;
                    this.errorMessage = error.error.message;
                },
                complete: () => {
                    this.router.navigate([`/groups`]);
                }
            })
        } else {
            let dto = new UpdateGroupDto(this.form.value.name, this.selectedStudents, this.selectedSubjects);
            this.groupService.update(this.groupId, dto).subscribe({
                error: (error: any) => {
                    this.showError = true;
                    this.errorMessage = error.error.message;
                },
                complete: () => {
                    this.router.navigate([`/groups`]);
                }
            })
        }
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
}