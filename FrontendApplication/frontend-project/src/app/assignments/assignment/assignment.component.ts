import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AssignmentService } from '../../shared/services/assignment.service';
import { JWTTokenService } from '../../shared/services/jwt-token.service';
import { AssignmentAnswerDto, AssignmentDto, AssignmentResponseDto } from '../../shared/models/assignment.model';
import { assignmentLabels } from '../../shared/translations/assignment.translation';
import { joinVariables } from '../../shared/helpers/variables-join.helper';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrl: './assignment.component.css',
  imports: [CommonModule, FormsModule, ReactiveFormsModule]
})
export class AssignmentComponent {
  constructor(public assignmentService: AssignmentService, public jwtService: JWTTokenService,
    public router: Router, private route: ActivatedRoute, private fb: FormBuilder) {
  }

  form!: FormGroup;
  error = false;

  assignmentId!: number;
  assignmentDto!: AssignmentDto;
  variablesErrorRequired = assignmentLabels['variable-empty'];
  variablesErrorNotNumber = assignmentLabels['variable-not-number'];

  ngOnInit() {
    this.form = this.fb.group({
      variables: this.fb.array([])
    });

    this.route.paramMap.subscribe(params => {
      this.assignmentId = Number(params.get('id'));
      this.getById();
    });
  }

  get variables(): FormArray {
    return this.form.get('variables') as FormArray;
  }

  get variablesIndices(): number[] {
    return Array(this.variables.length).fill(0).map((_, i) => i);
  }

  getById() {
    for (let i = 0; i < 3; i++) {
      this.variables.push(this.fb.control('', [Validators.required, Validators.pattern(/^\d+(\.\d+)?$/)]))
    }
    this.assignmentService.getById(this.assignmentId).subscribe({
      next: (assignmentDto: AssignmentDto) => {
        this.assignmentDto = assignmentDto;
        // for (let i = 0; i < this.assignmentDto.variablesCount; i++) {
        //   this.variables.push(this.fb.control('', [Validators.required, Validators.pattern(/^\d+(\.\d+)?$/)])]))
        // }
      },
      error: (error: any) => {
        // this.router.navigate([`/error/404`]);
      }
    })
  }

  answer() {
    this.onValidate();

    if (this.form.invalid) {
      return;
    }

    let variableNames = [], variableValues = [];
    for (let i = 0; i < this.variables.length; i++) {
      variableNames.push(`x${i + 1}`);
      variableValues.push(this.variables.at(i).value);
    }
    let answer = joinVariables(variableNames, variableValues);

    this.assignmentService.answer(this.assignmentId, new AssignmentAnswerDto(answer))
      .subscribe({
        next: (assignmentResponseDto: AssignmentResponseDto) => {

        }
      })
  }

  stop() {
    this.assignmentService.stop(this.assignmentId).subscribe({
      next: () => {
        this.router.navigate([`/student/assignments`]);
      }
    })
  }

  onValidate() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
    }
  }

  finish() {
    this.assignmentService.finish(this.assignmentId).subscribe({
      next: () => {
        this.router.navigate([`/student/assignments`]);
      }
    })
  }
}
