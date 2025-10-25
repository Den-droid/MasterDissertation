import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

export const studentTeacherGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isStudent() || authService.isTeacher()) {
    return true;
  }

  return router.createUrlTree(['/', 'auth', 'signin']);
};
