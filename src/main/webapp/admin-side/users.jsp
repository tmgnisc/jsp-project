<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users Management - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }
        textarea, input[type="text"], input[type="email"], input[type="password"], select {
            width: 100%;
            padding: 8px;
        }
    </style>
</head>
<body class="bg-gray-100">
    <div class="flex h-screen">
        <!-- Sidebar -->
        <div class="w-64 bg-[#002B5B] text-white">
            <div class="p-4">
                <h2 class="text-2xl font-bold text-[#F4A300]">Admin Panel</h2>
            </div>
            <nav class="mt-8">
                <a href="dashboard" class="flex items-center px-4 py-3 bg-[#F4A300] text-white">
                    <i class="fas fa-tachometer-alt w-6"></i>
                    <span>Dashboard</span>
                </a>
                <a href="foods" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-utensils w-6"></i>
                    <span>Foods</span>
                </a>
                <a href="attractions" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-mountain w-6"></i>
                    <span>Attractions</span>
                </a>
                <a href="music" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-music w-6"></i>
                    <span>Music</span>
                </a>
                <a href="movies" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-film w-6"></i>
                    <span>Movies</span>
                </a>
                <a href="sports" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-running w-6"></i>
                    <span>Sports</span>
                </a>
                <a href="users" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-users w-6"></i>
                    <span>Users</span>
                </a>
                <a href="index" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-sign-out-alt w-6"></i>
                    <span>Logout</span>
                </a>
            </nav>
        </div>

        <!-- Main Content -->
        <div class="flex-1 overflow-auto">
            <!-- Top Bar -->
            <div class="bg-white shadow-md">
                <div class="flex justify-between items-center px-8 py-4">
                    <h1 class="text-2xl font-semibold text-[#002B5B]">Users Management</h1>
                    <div class="flex items-center space-x-4">
                        <span class="text-gray-600">Welcome, Admin</span>
                        <img src="https://ui-avatars.com/api/?name=Admin&background=002B5B&color=fff" alt="Admin" class="w-10 h-10 rounded-full">
                    </div>
                </div>
            </div>

            <!-- Notification -->
            <%
                String notify = (String) request.getAttribute("notify");
                if (notify != null && !notify.isEmpty()) {
                    String alertClass = notify.contains("successfully") ? "bg-green-100 border-green-500 text-green-700" : "bg-red-100 border-red-500 text-red-700";
            %>
                <div class="p-8">
                    <div class="<%= alertClass %> border-l-4 p-4 mb-6" role="alert">
                        <p><%= notify %></p>
                    </div>
                </div>
            <%
                }
            %>

            <!-- Content -->
            <div class="p-8">
                <!-- Add New User Button -->
                <div class="mb-6">
                    <button onclick="showAddUserModal()" class="bg-[#F4A300] text-white px-4 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                        <i class="fas fa-plus mr-2"></i>Add New User
                    </button>
                </div>

                <!-- Users Table -->
                <div class="bg-white rounded-lg shadow-md overflow-hidden">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Role</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            <%
                                List<User> userList = (List<User>) request.getAttribute("userList");
                                if (userList != null) {
                                    for (User item : userList) {
                            %>
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="flex items-center">
                                        <div class="flex-shrink-0 h-10 w-10">
                                            <img class="h-10 w-10 rounded-full" src="https://ui-avatars.com/api/?name=<%=item.getFullName().replace(" ", "+")%>&background=002B5B&color=fff" alt="">
                                        </div>
                                        <div class="ml-4">
                                            <div class="text-sm font-medium text-gray-900"><%=item.getFullName()%></div>
                                            <div class="text-sm text-gray-500">@<%=item.getUsername()%></div>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-900"><%=item.getEmail()%></div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                        <%=item.getRole().substring(0, 1).toUpperCase() + item.getRole().substring(1)%>
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                        <%=item.getStatus().substring(0, 1).toUpperCase() + item.getStatus().substring(1)%>
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <button onclick="showEditUserModal(<%=item.getId()%>)" class="text-[#F4A300] hover:text-[#A31621] mr-3">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <form action="${pageContext.request.contextPath}/users" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="<%=item.getId()%>">
                                        <button type="submit" class="text-red-600 hover:text-red-900" onclick="return confirm('Are you sure you want to delete this user?')">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                            <%
                                    }
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Add/Edit User Modal -->
    <div id="userModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden overflow-y-auto h-full w-full">
        <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div class="mt-3">
                <h3 class="text-lg font-medium text-[#002B5B] mb-4" id="modalTitle">Add New User</h3>
                <form id="userForm" action="${pageContext.request.contextPath}/users" method="post" class="space-y-4">
                    <input type="hidden" name="action" id="formAction" value="add">
                    <input type="hidden" name="id" id="userId" value="0">
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Full Name</label>
                        <input type="text" name="fullName" id="fullName" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Email</label>
                        <input type="email" name="email" id="email" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Username</label>
                        <input type="text" name="username" id="username" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Password</label>
                        <input type="password" name="password" id="password" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Role</label>
                        <select name="role" id="role" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                            <option value="user">User</option>
                            <option value="admin">Admin</option>
                        </select>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Status</label>
                        <select name="status" id="status" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                            <option value="active">Active</option>
                            <option value="inactive">Inactive</option>
                        </select>
                    </div>
                    <div class="flex justify-end space-x-3">
                        <button type="button" onclick="closeUserModal()" class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50">
                            Cancel
                        </button>
                        <button type="submit" class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-[#F4A300] hover:bg-[#A31621]">
                            Save
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        function showAddUserModal() {
            document.getElementById('modalTitle').textContent = 'Add New User';
            document.getElementById('formAction').value = 'add';
            document.getElementById('userId').value = '0';
            document.getElementById('userForm').reset();
            document.getElementById('userModal').classList.remove('hidden');
        }

        function showEditUserModal(id) {
            document.getElementById('modalTitle').textContent = 'Edit User';
            document.getElementById('formAction').value = 'edit';
            document.getElementById('userId').value = id;

            fetch('${pageContext.request.contextPath}/users?action=getUser&id=' + id)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        alert(data.error);
                        return;
                    }
                    document.getElementById('fullName').value = data.fullName || '';
                    document.getElementById('email').value = data.email || '';
                    document.getElementById('username').value = data.username || '';
                    document.getElementById('password').value = data.password || ''; // Note: In production, handle securely
                    document.getElementById('role').value = data.role || '';
                    document.getElementById('status').value = data.status || '';
                    document.getElementById('userModal').classList.remove('hidden');
                })
                .catch(error => {
                    console.error('Error fetching user:', error);
                    alert('Failed to load user data.');
                });
        }

        function closeUserModal() {
            document.getElementById('userModal').classList.add('hidden');
        }

        function deleteUser(id) {
            if (confirm('Are you sure you want to delete this user?')) {
                fetch('${pageContext.request.contextPath}/users', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'action=delete&id=' + id
                })
                .then(response => response.text())
                .then(data => {
                    location.reload();
                })
                .catch(error => {
                    console.error('Error deleting user:', error);
                    alert('Failed to delete user.');
                });
            }
        }
    </script>
</body>
</html>