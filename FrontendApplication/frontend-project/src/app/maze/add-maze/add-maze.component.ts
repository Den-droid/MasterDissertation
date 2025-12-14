import { CommonModule, Location } from "@angular/common";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { validationMessages } from "../../shared/translations/common.translation";
import { universityLabels } from "../../shared/translations/university.translation";
import { UniversityDto } from "../../shared/models/university.model";
import { UniversityService } from "../../shared/services/university.service";
import { MazeService } from "../../shared/services/maze.service";
import { AddMazeDto, MazePointDto } from "../../shared/models/maze.model";
import { mazePageTitles, mazeValidationMessages } from "../../shared/translations/maze.translation";

@Component({
    selector: 'app-add-maze',
    templateUrl: './add-maze.component.html',
    imports: [FormsModule, ReactiveFormsModule, CommonModule]
})
export class AddMazeComponent implements OnInit {
    form!: FormGroup;
    showError: boolean = false;
    errorMessage = '';
    showServerError: boolean = false;
    serverErrorMessage = '';
    generateButtonClicked = false;
    saveButtonClicked = false;
    isAddStartPointMode = false;
    isAddEndPointMode = false;

    pageTitle = mazePageTitles["add-maze"];
    nameRequiredMessage = validationMessages["name-required"];
    universityRequiredMessage = universityLabels["selection-required"];
    widthHeightRequiredMessage = mazeValidationMessages["width-height-less-1"];
    startEndRequired = mazeValidationMessages["start-end-required"];

    universities: UniversityDto[] = [];
    walls: boolean[][] = [];
    startPoint: MazePointDto | null = null;
    endPoint: MazePointDto | null = null;
    width: number = 0;
    height: number = 0;

    constructor(private location: Location, private fb: FormBuilder, private readonly router: Router,
        private universityService: UniversityService, private mazeService: MazeService
    ) {

    }

    get f() {
        return this.form.controls;
    }

    ngOnInit(): void {
        this.form = this.fb.group({
            name: ['', Validators.required],
            university: ['', Validators.required],
            width: [0],
            height: [0]
        });

        this.universityService.getAll().subscribe({
            next: (universities: UniversityDto[]) => {
                this.universities = universities;
            }
        })
    }

    generateTemplate() {
        this.generateButtonClicked = true;

        if (this.form.value.width < 1 || this.form.value.height < 1) {
            return;
        }

        this.width = this.form.value.width;
        this.height = this.form.value.height;

        this.walls = [];
        this.startPoint = null;
        this.endPoint = null;
        for (let i = 0; i < this.height; i++) {
            this.walls[i] = [];
            for (let j = 0; j < this.width; j++) {
                this.walls[i].push(false)
            }
        }

        if (this.saveButtonClicked) {
            this.showError = true;
            this.errorMessage = this.startEndRequired;
            return;
        } else {
            this.showError = false;
        }
    }

    clickCell(x: number, y: number) {
        let wallRemoved = false;
        let startPointRemoved = false;
        let endPointRemoved = false;
        if (this.startPoint && this.startPoint.x == x && this.startPoint.y == y) {
            this.startPoint = null;
            startPointRemoved = true;
        } else if (this.endPoint && this.endPoint.x == x && this.endPoint.y == y) {
            this.endPoint = null;
            endPointRemoved = true;
        } else {
            for (let i = 0; i < this.walls.length; i++) {
                for (let j = 0; j < this.walls[i].length; j++) {
                    if (this.walls[i][j] && i == y && j == x) {
                        this.walls[i][j] = false;
                        wallRemoved = true;
                        break;
                    }
                }
            }
        }

        if (this.isAddStartPointMode) {
            if (!startPointRemoved)
                this.startPoint = new MazePointDto(x, y);
        } else if (this.isAddEndPointMode) {
            if (!endPointRemoved)
                this.endPoint = new MazePointDto(x, y);
        } else {
            if (!wallRemoved)
                this.walls[y][x] = true;
        }

        if (this.saveButtonClicked && (!this.startPoint || !this.endPoint)) {
            this.showError = true;
            this.errorMessage = this.startEndRequired;
            return;
        } else {
            this.showError = false;
        }
    }

    clearSelection() {
        this.startPoint = null;
        this.endPoint = null;
        for (let i = 0; i < this.walls.length; i++) {
            this.walls[i].fill(false, 0, this.walls[i].length);
        }

        if (this.saveButtonClicked) {
            this.showError = true;
            this.errorMessage = this.startEndRequired;
            return;
        } else {
            this.showError = false;
        }
    }

    toggleEndStartPointMode() {
        this.isAddEndPointMode = !this.isAddEndPointMode;
    }

    toggleAddStartPointMode() {
        this.isAddStartPointMode = !this.isAddStartPointMode;
    }

    save() {
        this.saveButtonClicked = true;
        this.validate();

        if (this.form.invalid) {
            return;
        }

        if (!this.startPoint || !this.endPoint) {
            this.showError = true;
            this.errorMessage = this.startEndRequired;
            return;
        }

        let startPoint = new MazePointDto(this.startPoint.x, this.startPoint.y);
        let endPoint = new MazePointDto(this.endPoint.x, this.endPoint.y);
        let walls: MazePointDto[] = [];
        for (let i = 0; i < this.walls.length; i++) {
            for (let j = 0; j < this.walls[i].length; j++) {
                if (this.walls[i][j]) {
                    walls.push(new MazePointDto(j, i));
                }
            }
        }
        let addMazeDto = new AddMazeDto(this.form.value.name, this.width,
            this.height, this.form.value.university, startPoint, endPoint,
            walls
        )

        this.mazeService.add(addMazeDto).subscribe({
            error: (err: any) => {
                this.showServerError = true;
                this.serverErrorMessage = err.error.message;
            },
            complete: () => {
                this.router.navigate([`assignments`]);
            }
        })
    }

    createRange(n: number): number[] {
        return Array.from({ length: n }, (_, i) => i + 1);
    }

    goBack() {
        this.location.back();
    }

    validate() {
        if (this.form.invalid) {
            this.f['name'].markAsTouched();
            this.f['university'].markAsTouched();
            return;
        }
    }

    isStartCell(x: number, y: number) {
        return this.startPoint && this.startPoint.x == x && this.startPoint.y == y;
    }

    isEndCell(x: number, y: number) {
        return this.endPoint && this.endPoint.x == x && this.endPoint.y == y;
    }

    isWallCell(x: number, y: number) {
        for (let i = 0; i < this.walls.length; i++) {
            for (let j = 0; j < this.walls[i].length; j++) {
                if (this.walls[i][j] && i == y && j == x) {
                    return true;
                }
            }
        }
        return false;
    }

    isRegularCell(x: number, y: number) {
        return !this.isStartCell(x, y) && !this.isEndCell(x, y) && !this.isWallCell(x, y);
    }

    isAddStartPointButtonDisabled() {
        return this.isAddEndPointMode;
    }

    isAddEndPointButtonDisabled() {
        return this.isAddStartPointMode;
    }

    isClearSelectionButtonDisabled() {
        return this.isAddStartPointMode || this.isAddEndPointMode;;
    }
}