export enum AssignmentRestrictionType {
  N_ATTEMPTS, ATTEMPT_PER_N_MINUTES, DEADLINE
}

export enum AssignmentRestrictionTypeLabel {
  N_ATTEMPTS = 'Кількість спроб', 
  ATTEMPT_PER_N_MINUTES = 'Кількість хвилин на 1 спробу', 
  DEADLINE = 'Дедлайн'
}