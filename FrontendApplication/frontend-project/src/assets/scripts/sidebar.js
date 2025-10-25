function toggleSidebar() {
  const sidebar = document.getElementById('sidebar');
  const main = document.querySelector('.main');

  sidebar.classList.toggle('expand');
  main.classList.toggle('main-expand');
  main.classList.toggle('main-not-expand');
}
