import { ApplicationConfig, inject, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AssignmentsComponent } from './assignments/assignments.component';
import { AuthComponent } from './auth/auth.component';
import { ErrorComponent } from './error/error.component';
import { authenticatedGuard } from './shared/guards/authenticated.guard';
import { studentGuard } from './shared/guards/student.guard';
import { AuthService } from './shared/services/auth.service';
import { StudentComponent } from './student/student.component';
import { UniversityComponent } from './university/university.component';
import { adminGuard } from './shared/guards/admin.guard';
import { adminTeacherGuard } from './shared/guards/admin-teacher.guard';
import { SubjectComponent } from './subjects/subject.component';
import { FunctionComponent } from './function/function.component';
import { authorizeInterceptor } from './shared/interceptors/authorize.interceptor';
import { UsersComponent } from './users/users.component';
import { UserComponent } from './user/user.component';

const routes: Routes = [
  {
    path: "", redirectTo: () => {
      const authService = inject(AuthService);

      if (authService.isStudent()) {
        return '/student/assignments';
      } else if (authService.isTeacher()) {
        return '/teacher/assignments';
      } else if (authService.isAdmin()) {
        return '/users';
      }

      return '/auth/signin';
    },
    pathMatch: 'full'
  },
  {
    path: "auth", component: AuthComponent,
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: "error", component: ErrorComponent,
    loadChildren: () => import('./error/error.module').then(m => m.ErrorModule)
  },
  {
    path: "student", component: StudentComponent,
    loadChildren: () => import('./student/student.module').then(m => m.StudentModule),
    canActivate: [studentGuard]
  },
  {
    path: "assignments", component: AssignmentsComponent,
    loadChildren: () => import('./assignments/assignments.module').then(m => m.AssignmentsModule),
    canActivate: [authenticatedGuard]
  },
  {
    path: "user", component: UserComponent,
    loadChildren: () => import('./user/user.module').then(m => m.UserModule),
    canActivate: [authenticatedGuard]
  },
  {
    path: "universities", component: UniversityComponent,
    loadChildren: () => import('./university/university.module').then(m => m.UniversityModule),
    canActivate: [adminGuard]
  },
  {
    path: "subjects", component: SubjectComponent,
    loadChildren: () => import('./subjects/subject.module').then(m => m.SubjectModule),
    canActivate: [adminTeacherGuard]
  },
  {
    path: "functions", component: FunctionComponent,
    loadChildren: () => import('./function/function.module').then(m => m.FunctionModule),
    canActivate: [adminTeacherGuard]
  },
  {
    path: "users", component: UsersComponent,
    loadChildren: () => import('./users/users.module').then(m => m.UsersModule),
    canActivate: [adminGuard]
  },
  { path: '**', redirectTo: '/error/404', pathMatch: 'full' }
]

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authorizeInterceptor])),
  ]
};
