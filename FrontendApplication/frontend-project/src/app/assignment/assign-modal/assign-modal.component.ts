import { Component, Input, Output, EventEmitter } from "@angular/core";
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators, FormControl } from "@angular/forms";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { SubjectDto } from "../../shared/models/subject.model";
import { functionLabels } from "../../shared/translations/function.translation";
import { assignModalHeader } from "../../shared/translations/assignment.translation";
import { CommonModule } from "@angular/common";
import { Subject } from "rxjs";

@Component({
    selector: 'app-assign-modal',
    templateUrl: './assign-modal.component.html',
    imports: [FormsModule, ReactiveFormsModule, CommonModule]
})
export class AssignModalComponent {
    @Input() errorSubject$!: Subject<string>;

    private _subjects: SubjectDto[] = [];
    @Input()
    set subjects(val: SubjectDto[]) {
        this._subjects = val;
        this.currentSubjects = val;
        this.displayedSubjects['0'] = val;

        (this.form.get('subjects') as FormGroup).valueChanges.subscribe({
            next: (obj: { [key: string]: any }) => {
                if (this.programmaticChange) return;

                this.currentSubjects = [...this.subjects];
                for (let selectedSubject of Object.values(obj)) {
                    if (selectedSubject !== '') {
                        let index = this.currentSubjects.findIndex(cs => cs.id == Number.parseInt(selectedSubject));
                        this.currentSubjects.splice(index, 1);
                    }
                }

                for (let selectedSubjectKey of Object.keys(obj)) {
                    let currentDisplayedSubjects = [...this.currentSubjects];

                    let index = this.subjects.findIndex(s => s.id == obj[selectedSubjectKey]);
                    if (index !== -1)
                        currentDisplayedSubjects.push(this.subjects[index]);

                    this.displayedSubjects[selectedSubjectKey] = currentDisplayedSubjects;
                }
            }
        })
    }
    get subjects() {
        return this._subjects;
    }

    @Output() saveAttempt = new EventEmitter();

    title = assignModalHeader;
    currentSubjects: SubjectDto[] = [];
    displayedSubjects: { [key: string]: any } = {};

    subjectRequiredMessage = functionLabels["subject-selection-required"];
    errorMessage!: string;

    form!: FormGroup;

    programmaticChange = false;

    constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.errorSubject$.subscribe(msg => {
            this.errorMessage = msg;
        });

        this.form = this.fb.group({
            subjects: this.fb.group({}),
        });

        (this.form.get('subjects') as FormGroup).addControl('0', new FormControl('', Validators.required));
    }

    get f() {
        return this.form.controls;
    }

    removeSubject(index: number) {
        const group = (this.form.get('subjects') as FormGroup);
        delete this.displayedSubjects[Object.keys(group.controls)[index]];

        if (Object.keys(group.controls).length === 1) {
            group.get(Object.keys(group.controls)[index])?.setValue('');
        } else {
            group.removeControl(Object.keys(group.controls)[index]);
        }

    }

    addSubject() {
        this.programmaticChange = true;

        const group = (this.form.get('subjects') as FormGroup);
        const controlNames = Object.keys(group.controls);
        this.displayedSubjects[((Number.parseInt(controlNames[controlNames.length - 1]) + 1)).toString()]
            = [...this.currentSubjects];

        group.addControl(((Number.parseInt(controlNames[controlNames.length - 1]) + 1)).toString(), new FormControl('',
            Validators.required
        ));

        this.programmaticChange = false;
    }

    save() {
        this.validate();

        if (this.form.invalid) {
            return;
        }

        let ids = [];
        let values = (Object.values(this.form.value.subjects) as string[]);
        for (let id of values)
            ids.push(Number.parseInt(id))

        this.saveAttempt.emit(values);
    }

    validate() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
    }

    isLastSubject(index: number) {
        const group = (this.form.get('subjects') as FormGroup);
        const controlNames = Object.keys(group.controls);
        return controlNames.length - 1 === index;
    }

    getSubjectControlName(index: number) {
        const group = (this.form.get('subjects') as FormGroup);
        return Object.keys(group.controls)[index];
    }

    getSelectedSubjects() {
        return Object.values(this.form.value.subjects);
    }
}