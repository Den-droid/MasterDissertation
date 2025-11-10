import { Component, Output, EventEmitter, Input } from "@angular/core";
import { FormsModule, ReactiveFormsModule, FormGroup, FormBuilder, Validators } from "@angular/forms";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { validationMessages } from "../../shared/translations/common.translation";
import { markModalHeader, markModalValidation } from "../../shared/translations/assignment.translation";
import { MarkModalDto } from "../../shared/models/mark.model";
import { integerValidator } from "../../shared/validators/number.validator";

@Component({
    selector: 'app-assignment-mark-modal',
    templateUrl: './mark-modal.component.html',
    imports: [FormsModule, ReactiveFormsModule]
})
export class MarkModalComponent {
    @Input()
    set inputValue(inputValue: MarkModalDto) {
        if (this.form) {
            this.form.get('mark')?.setValue(inputValue.mark);
            this.form.get('comment')?.setValue(inputValue.comment);
        }
    }

    @Output() saveAttempt = new EventEmitter();
    title = markModalHeader;

    integerRequiredMessage = validationMessages['integer-required'];
    markRequiredMessage = markModalValidation["mark-required"];
    commentRequiredMessage = markModalValidation["comment-required"];

    form!: FormGroup;

    constructor(public activeModal: NgbActiveModal, private fb: FormBuilder) {
    }

    ngOnInit(): void {
        this.form = this.fb.group({
            mark: ['', [Validators.required, integerValidator()]],
            comment: ['', [Validators.required]]
        });
    }

    get f() {
        return this.form.controls;
    }

    save() {
        this.validate();

        if (this.form.invalid) {
            return;
        }

        this.saveAttempt.emit(new MarkModalDto(Number.parseInt(this.form.value.mark), this.form.value.comment));
    }

    validate() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
    }
}