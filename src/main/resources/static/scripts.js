fetch('http://localhost:9000/users')
.then(res => res.json())
.then(users => {
  let tableHtml = ''

  for (let index in users) {
      const user = users[index]
      tableHtml += userRow(user)
  }

  document.getElementById('usersBody').innerHTML = tableHtml
})

function userRow(user){

const userId = user.id

const tr = `<tr id="user-${userId}"><td>${userId}</td><td class="firstName">${user.firstName}</td>
<td class="lastName">${user.lastName}</td><td style="width: 25px; height: 25px; padding: 5px;"><button onclick="
deleteUser(${userId})" id="delete" type="button"> \u2718 </button></td>
<td style="width: 25px; height: 25px padding: 5px;"><button onclick="openForm(${userId})" id="update" type="button">\u2700</button></td></tr>`

return tr
}

function deleteUser (userId){

fetch(`http://localhost:9000/users/${userId}`, { method: 'DELETE' })
.then(() => {
  let row = document.getElementById(`user-${userId}`)
  row.remove();
})
}

function updateUser (userId) {

  const newFirstName = document.getElementById('newFirstName').value
  const newLastName = document.getElementById('newLastName').value

  fetch(`http://localhost:9000/users/${userId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({firstName : newFirstName, lastName : newLastName })
  })

  .then(() => {

    const row = document.getElementById(`user-${userId}`)

    row.querySelector('.firstName').innerHTML = newFirstName
    row.querySelector('.lastName').innerHTML = newLastName
  })
}

function openForm(userId) {
  const updateUserForm = document.getElementById("myForm")
  updateUserForm.style.display = "block";
  //row.querySelector('.firstName').innerHTML
  const row = document.getElementById(`user-${userId}`)
  updateUserForm.querySelector('#newFirstName').value = row.querySelector('.firstName').innerHTML
  updateUserForm.querySelector('#newLastName').value = row.querySelector('.lastName').innerHTML
  const submitButton = updateUserForm.querySelector('button[type="submit"]')
  submitButton.addEventListener("click", function () {console.log('test')

    updateUser(userId)
  })

}

function closeForm() {
  document.getElementById("myForm").style.display = "none";
}

function addUser(){
const userName = document.getElementById("name");
const userSecondName = document.getElementById("secondName");

fetch(`http://localhost:9000/users`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({firstName : userName.value, lastName : userSecondName.value })
})
.then(res => res.json())
.then(user => {
let userTableBodyElement = document.getElementById('usersBody')
let tableHtml = userTableBodyElement.innerHTML + userRow(user)
userTableBodyElement.innerHTML = tableHtml
})
}
