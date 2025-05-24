<%@page import="model.FoodItem"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Foods Management - Nepal Navigator</title>
<script src="https://cdn.tailwindcss.com"></script>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<style>
    body {
        font-family: 'Poppins', sans-serif;
    }
    textarea, input[type="text"], select {
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
                <a href="dashboard.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-tachometer-alt w-6"></i>
                    <span>Dashboard</span>
                </a>
                <a href="${pageContext.request.contextPath}/food" class="flex items-center px-4 py-3 bg-[#F4A300] text-white">
                    <i class="fas fa-utensils w-6"></i>
                    <span>Foods</span>
                </a>
                <a href="attractions.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-mountain w-6"></i>
                    <span>Attractions</span>
                </a>
                <a href="music.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-music w-6"></i>
                    <span>Music</span>
                </a>
                <a href="movies.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-film w-6"></i>
                    <span>Movies</span>
                </a>
                <a href="sports.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-running w-6"></i>
                    <span>Sports</span>
                </a>
                <a href="users.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
                    <i class="fas fa-users w-6"></i>
                    <span>Users</span>
                </a>
                <a href="../index.html" class="flex items-center px-4 py-3 text-gray-300 hover:bg-[#F4A300] hover:text-white">
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
                    <h1 class="text-2xl font-semibold text-[#002B5B]">Foods Management</h1>
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
            %>
                <div class="p-8">
                    <div class="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-6" role="alert">
                        <p><%= notify %></p>
                    </div>
                </div>
            <%
                }
            %>

            <!-- Content -->
            <div class="p-8">
                <!-- Add New Food Button -->
                <div class="mb-6">
                    <button onclick="showAddFoodModal()" class="bg-[#F4A300] text-white px-4 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                        <i class="fas fa-plus mr-2"></i>Add New Food
                    </button>
                </div>

                <!-- Foods Table -->
                <div class="bg-white rounded-lg shadow-md overflow-hidden">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Image</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Name</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Category</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            <%
                                List<FoodItem> foodItems = (List<FoodItem>) request.getAttribute("foodItems");
                                if (foodItems != null) {
                                    for (FoodItem item : foodItems) {
                            %>
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <img src="${pageContext.request.contextPath}<%=item.getImage() != null ? item.getImage() : "/images/placeholder.jpg"%>" alt="Food" class="w-16 h-16 object-cover rounded" onerror="this.src='https://via.placeholder.com/100'">
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm font-medium text-gray-900"><%=item.getName()%></div>
                                </td>
                                <td class="px-6 py-4">
                                    <div class="text-sm text-gray-500"><%=item.getDescription()%></div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800"><%=item.getCategory()%></span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                                    <button onclick="showEditFoodModal(<%=item.getId()%>)" class="text-[#F4A300] hover:text-[#A31621] mr-3">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <form action="${pageContext.request.contextPath}/food" method="post" style="display:inline;">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="<%=item.getId()%>">
                                        <button type="submit" class="text-red-600 hover:text-red-900" onclick="return confirm('Are you sure you want to delete this food item?')">
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

    <!-- Add/Edit Food Modal -->
    <div id="foodModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden overflow-y-auto h-full w-full">
        <div class="relative top-20 mx-auto p-5 border w-[600px] shadow-lg rounded-md bg-white">
            <div class="mt-3">
                <h3 class="text-lg font-medium text-[#002B5B] mb-4" id="modalTitle">Add New Food</h3>
                <form id="foodForm" action="${pageContext.request.contextPath}/food" method="post" enctype="multipart/form-data" class="space-y-4">
                    <input type="hidden" name="action" id="formAction" value="add">
                    <input type="hidden" name="id" id="foodId" value="0">
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Food Image</label>
                        <input type="file" name="image" accept="image/*" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Food Name</label>
                        <input type="text" name="name" id="foodName" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Description</label>
                        <textarea name="description" id="description" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" rows="3" required></textarea>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Category</label>
                        <select name="category" id="category" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" required>
                            <option value="">Select Category</option>
                            <option value="Dumplings">Dumplings</option>
                            <option value="Curry">Curry</option>
                            <option value="Soup">Soup</option>
                            <option value="Dessert">Dessert</option>
                            <option value="Snacks">Snacks</option>
                            <option value="Main Course">Main Course</option>
                        </select>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Ingredients</label>
                        <textarea name="ingredients" id="ingredients" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" rows="3" required></textarea>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Preparation Method</label>
                        <textarea name="preparationMethod" id="preparationMethod" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" rows="3" required></textarea>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Serving Suggestions</label>
                        <textarea name="servingSuggestions" id="servingSuggestions" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" rows="2" required></textarea>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Cultural Significance</label>
                        <textarea name="culturalSignificance" id="culturalSignificance" class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#F4A300] focus:border-[#F4A300] sm:text-sm" rows="2" required></textarea>
                    </div>
                    <div class="flex justify-end space-x-3">
                        <button type="button" onclick="closeFoodModal()" class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50">
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
        function showAddFoodModal() {
            document.getElementById('modalTitle').textContent = 'Add New Food';
            document.getElementById('formAction').value = 'add';
            document.getElementById('foodId').value = '0';
            document.getElementById('foodForm').reset();
            document.getElementById('foodModal').classList.remove('hidden');
        }

        function showEditFoodModal(id) {
            document.getElementById('modalTitle').textContent = 'Edit Food';
            document.getElementById('formAction').value = 'edit';
            document.getElementById('foodId').value = id;

            <%
                FoodItem foodItemToEdit = (FoodItem) request.getAttribute("foodItemToEdit");
                if (foodItemToEdit != null) {
            %>
                document.getElementById('foodName').value = '<%= foodItemToEdit.getName() != null ? foodItemToEdit.getName() : "" %>';
                document.getElementById('description').value = '<%= foodItemToEdit.getDescription() != null ? foodItemToEdit.getDescription() : "" %>';
                document.getElementById('category').value = '<%= foodItemToEdit.getCategory() != null ? foodItemToEdit.getCategory() : "" %>';
                document.getElementById('ingredients').value = '<%= foodItemToEdit.getIngredients() != null ? foodItemToEdit.getIngredients() : "" %>';
                document.getElementById('preparationMethod').value = '<%= foodItemToEdit.getPreparationMethod() != null ? foodItemToEdit.getPreparationMethod() : "" %>';
                document.getElementById('servingSuggestions').value = '<%= foodItemToEdit.getServingSuggestions() != null ? foodItemToEdit.getServingSuggestions() : "" %>';
                document.getElementById('culturalSignificance').value = '<%= foodItemToEdit.getCulturalSignificance() != null ? foodItemToEdit.getCulturalSignificance() : "" %>';
            <%
                }
            %>

            document.getElementById('foodModal').classList.remove('hidden');
        }

        function closeFoodModal() {
            document.getElementById('foodModal').classList.add('hidden');
        }
    </script>
</body>
</html>