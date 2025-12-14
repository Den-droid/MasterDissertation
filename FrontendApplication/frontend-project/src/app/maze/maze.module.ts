import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AddMazeComponent } from './add-maze/add-maze.component';
import { mazePageTitles } from '../shared/translations/maze.translation';

const mazeRoutes: Routes = [
  { path: "add", component: AddMazeComponent, title: mazePageTitles['add-maze'] }
]

@NgModule({
  imports: [
    RouterModule.forChild(mazeRoutes)
  ],
  exports: [RouterModule],
  bootstrap: []
})
export class MazeModule { }
