import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { InfoModalComponent } from '../../shared/modals/info/info-modal.component';
import { ConfirmModalComponent } from '../../shared/modals/confirm/confirm-modal.component';
import { modalText } from '../../shared/translations/common.translation';
import { SubjectDto } from '../../shared/models/subject.model';
import { SubjectService } from '../../shared/services/subject.service';
import { ActionType } from '../../shared/constants/action-type';
import { UniversityService } from '../../shared/services/university.service';
import { UniversityDto } from '../../shared/models/university.model';
import { FunctionService } from '../../shared/services/function.service';
import { FunctionDto } from '../../shared/models/function.model';
import { FunctionModalComponent } from '../function-modal/function-modal.component';
import { functionModal } from '../../shared/translations/function.translation';
import { JoinPipe } from "../../shared/pipes/join.pipe";
import { RestrictionModalComponent } from '../../shared/modals/restriction/restriction-modal.component';
import { DefaultRestrictionDto, ModalRestrictionDto, RestrictionDto } from '../../shared/models/restriction.model';
import { AssignmentRestrictionService } from '../../shared/services/assignment-restriction.service';
import { restrictionModalHeaders } from '../../shared/translations/restriction.translation';
import { DefaultRestrictionLevel } from '../../shared/constants/default-restriction-level.constant';

@Component({
  selector: 'app-function-list',
  templateUrl: './function-list.component.html',
  imports: [FormsModule, JoinPipe]
})
export class FunctionListComponent implements OnInit {
  functions: FunctionDto[] = [];
  filteredFunctions: FunctionDto[] = [];
  editedFunction!: FunctionDto;

  searchQuery: string = '';
  searchSubject: Subject<string> = new Subject<string>();

  constructor(private modalService: NgbModal, private subjectService: SubjectService,
    private universityService: UniversityService, private functionService: FunctionService,
    private restrictionService: AssignmentRestrictionService
  ) { }

  ngOnInit(): void {
    this.getFunctions();

    this.searchSubject.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.filteredFunctions = this.functions.filter(f =>
        f.text.toLowerCase().includes(value.toLowerCase())
      );
    });
  }

  getFunctionById(id: number) {
    return this.functionService.getById(id);
  }

  getFunctions() {
    this.functionService.getAll().subscribe({
      next: (functionsDto: FunctionDto[]) => {
        this.functions = functionsDto;
        this.filteredFunctions = functionsDto;
      }
    })
  }

  openAddModal() {
    const modalRef = this.modalService.open(FunctionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = functionModal['add-title'];
    modalRef.componentInstance.errorSubject$ = errorSubject;
    modalRef.componentInstance.actionType = ActionType.CREATE;

    this.universityService.getAll().subscribe({
      next: (universities: UniversityDto[]) => {
        modalRef.componentInstance.universities = universities;
      }
    })

    this.subjectService.getAll().subscribe({
      next: (subjects: SubjectDto[]) => {
        modalRef.componentInstance.subjects = subjects;
      }
    })

    modalRef.componentInstance.saveAttempt.subscribe(
      (result: any) => {
        this.functionService.add(result).subscribe({
          error: (error: any) => {
            errorSubject.next(error.error.message);
          },
          complete: () => {
            modalRef.close();
            this.getFunctions();
          }
        })
      }
    );
  }

  openEditModal(id: number) {
    const modalRef = this.modalService.open(FunctionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    this.getFunctionById(id).subscribe({
      next: (functionDto: FunctionDto) => {
        modalRef.componentInstance.inputValue = functionDto;
      }
    })

    this.universityService.getAll().subscribe({
      next: (universities: UniversityDto[]) => {
        modalRef.componentInstance.universities = universities;
      }
    })

    this.subjectService.getAll().subscribe({
      next: (subjects: SubjectDto[]) => {
        modalRef.componentInstance.subjects = subjects;
      }
    })

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = functionModal['edit-title'];
    modalRef.componentInstance.errorSubject$ = errorSubject;
    modalRef.componentInstance.actionType = ActionType.UPDATE;

    modalRef.componentInstance.saveAttempt.subscribe(
      (result: any) => {
        this.functionService.update(id, result).subscribe({
          error: (error: any) => {
            errorSubject.next(error.error.message);
          },
          complete: () => {
            modalRef.close();
            this.getFunctions();
          }
        })
      }
    );
  }

  deleteFunction(id: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = modalText['confirm-content']
    modalRef.componentInstance.title = modalText['confirm-title'];

    modalRef.result.then(
      () => {
        this.subjectService.delete(id).subscribe({
          error: (error: any) => {
            const deleteErrorModalRef = this.modalService.open(InfoModalComponent, {
              centered: true,
              backdrop: 'static',
              keyboard: false
            });

            deleteErrorModalRef.componentInstance.content = [error.error.message];
            deleteErrorModalRef.componentInstance.title = functionModal['delete-error'];
          },
          complete: () => {
            this.getFunctions();
          }
        })
      }
    ).catch(
      () => { return; }
    )
  }

  setDefaultRestriction(id: number) {
    const modalRef = this.modalService.open(RestrictionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    this.restrictionService.getDefaultForFunction(id).subscribe({
      next: (dto: DefaultRestrictionDto[]) => {
        if (dto.length > 0) {
          if (dto[0].functionId == null) {
            modalRef.componentInstance.isInputRestrictionTypeDifferent = true;
            if (dto[0].universityId != null) {
              modalRef.componentInstance.inputRestritionTypeLevel = DefaultRestrictionLevel.UNIVERSITY;
            } else {
              modalRef.componentInstance.inputRestritionTypeLevel = DefaultRestrictionLevel.SUBJECT;
            }
          }
          modalRef.componentInstance.inputValue = new ModalRestrictionDto(dto[0].restrictionType,
            dto[0].attemptsRemaining, dto[0].minutesForAttempt, dto[0].deadline);
        }
      }
    }
    )

    modalRef.componentInstance.title = restrictionModalHeaders.default;

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: ModalRestrictionDto) => {
        let dto = new DefaultRestrictionDto(null, value.restrictionType, null, null, id, null,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setDefaultRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getFunctions();
          }
        })
      }
    );
  }

  setRestriction(id: number) {
    const modalRef = this.modalService.open(RestrictionModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.title = restrictionModalHeaders['non-default'];

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: ModalRestrictionDto) => {
        let dto = new RestrictionDto(value.restrictionType, null, null, id, null, null,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getFunctions();
          }
        })
      }
    );
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }
}
