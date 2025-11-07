import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { UniversityModalComponent } from '../university-modal/university-modal.component';
import { UniversityService } from '../../shared/services/university.service';
import { AddUniversityDto, UpdateUniversityDto, UniversityDto } from '../../shared/models/university.model';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { InfoModalComponent } from '../../shared/modals/info/info-modal.component';
import { universityModal } from '../../shared/translations/university.translation';
import { ConfirmModalComponent } from '../../shared/modals/confirm/confirm-modal.component';
import { modalText } from '../../shared/translations/common.translation';
import { RestrictionModalComponent } from '../../shared/modals/restriction/restriction-modal.component';
import { restrictionModalHeaders } from '../../shared/translations/restriction.translation';
import { AssignmentRestrictionService } from '../../shared/services/assignment-restriction.service';
import { RestrictionDto, ModalRestrictionDto, DefaultRestrictionDto } from '../../shared/models/restriction.model';

@Component({
  selector: 'app-university-list',
  templateUrl: './university-list.component.html',
  imports: [FormsModule]
})
export class UniversityListComponent implements OnInit {
  universities: UniversityDto[] = [];
  filteredUniversities: UniversityDto[] = [];
  editedUniversity!: UniversityDto;

  searchQuery: string = '';
  searchSubject: Subject<string> = new Subject<string>();

  constructor(private modalService: NgbModal, private universityService: UniversityService,
    private restrictionService: AssignmentRestrictionService
  ) { }

  ngOnInit(): void {
    this.getUniversities();

    this.searchSubject.pipe(
      debounceTime(500)
    ).subscribe(value => {
      this.filteredUniversities = this.universities.filter(u =>
        u.name.toLowerCase().includes(value.toLowerCase())
      );
    });
  }

  getUniversityById(id: number) {
    return this.universityService.getById(id);
  }

  getUniversities() {
    this.universityService.getAll().subscribe({
      next: (universitiesDto: UniversityDto[]) => {
        this.universities = universitiesDto;
        this.filteredUniversities = universitiesDto;
      }
    })
  }

  openAddModal() {
    const modalRef = this.modalService.open(UniversityModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = universityModal['add-title'];
    modalRef.componentInstance.errorSubject$ = errorSubject;

    modalRef.componentInstance.saveAttempt.subscribe(
      (name: string) => {
        this.universityService.add(new AddUniversityDto(name)).subscribe({
          error: (error: any) => {
            errorSubject.next(error.error.message);
          },
          complete: () => {
            modalRef.close();
            this.getUniversities();
          }
        })
      }
    );
  }

  openEditModal(id: number) {
    const modalRef = this.modalService.open(UniversityModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    this.getUniversityById(id).subscribe({
      next: (university: UniversityDto) => {
        modalRef.componentInstance.inputValue = university.name;
      }
    })

    const errorSubject = new Subject<string>();

    modalRef.componentInstance.title = universityModal['edit-title'];
    modalRef.componentInstance.errorSubject$ = errorSubject;

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: string) => {
        this.universityService.update(id, new UpdateUniversityDto(value)).subscribe({
          error: (error: any) => {
            errorSubject.next(error.error.message);
          },
          complete: () => {
            modalRef.close();
            this.getUniversities();
          }
        })
      }
    );
  }

  deleteUniversity(id: number) {
    const modalRef = this.modalService.open(ConfirmModalComponent, {
      centered: true,
      backdrop: 'static',
      keyboard: false
    });

    modalRef.componentInstance.content = modalText['confirm-content']
    modalRef.componentInstance.title = modalText['confirm-title'];

    modalRef.result.then(
      () => {
        this.universityService.delete(id).subscribe({
          error: (error: any) => {
            const deleteErrorModalRef = this.modalService.open(InfoModalComponent, {
              centered: true,
              backdrop: 'static',
              keyboard: false
            });

            deleteErrorModalRef.componentInstance.content = error.error.message;
            deleteErrorModalRef.componentInstance.title = universityModal['delete-error'];
          },
          complete: () => {
            this.getUniversities();
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

    this.restrictionService.getDefaultForUniversity(id).subscribe({
      next: (dto: DefaultRestrictionDto[]) => {
        if (dto.length > 0)
          modalRef.componentInstance.inputValue = new ModalRestrictionDto(dto[0].restrictionType,
            dto[0].attemptsRemaining, dto[0].minutesForAttempt, dto[0].deadline);
      }
    }
    )

    modalRef.componentInstance.title = restrictionModalHeaders.default;

    modalRef.componentInstance.saveAttempt.subscribe(
      (value: ModalRestrictionDto) => {
        let dto = new DefaultRestrictionDto(null, value.restrictionType, id, null, null,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setDefaultRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getUniversities();
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
        let dto = new RestrictionDto(value.restrictionType, id, null, null, null,
          value.attemptsRemaining, value.minutesForAttempt, value.deadline);
        this.restrictionService.setRestriction(dto).subscribe({
          complete: () => {
            modalRef.close();
            this.getUniversities();
          }
        })
      }
    );
  }

  onSearchChange(value: string) {
    this.searchSubject.next(value);
  }
}
