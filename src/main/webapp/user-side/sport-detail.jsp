<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Sport, model.SportComment, java.util.List, java.time.LocalDateTime, java.time.ZoneId, java.time.temporal.ChronoUnit" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sport Detail - Nepal Navigator</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Poppins', sans-serif;
        }
        .image-gallery img {
            transition: transform 0.3s ease;
        }
        .image-gallery img:hover {
            transform: scale(1.05);
        }
    </style>
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-[#002B5B] text-white shadow-lg">
        <div class="max-w-7xl mx-auto px-4">
            <div class="flex justify-between items-center h-16">
                <div class="flex items-center">
                    <a href="${pageContext.request.contextPath}/index" class="text-2xl font-bold text-[#F4A300]">Nepal Navigator</a>
                </div>
                <div class="flex items-center space-x-4">
                    <a href="${pageContext.request.contextPath}/foods" class="hover:text-[#F4A300]">Foods</a>
                    <a href="${pageContext.request.contextPath}/attractions" class="hover:text-[#F4A300]">Attractions</a>
                    <a href="${pageContext.request.contextPath}/music" class="hover:text-[#F4A300]">Music</a>
                    <a href="${pageContext.request.contextPath}/movies" class="hover:text-[#F4A300]">Movies</a>
                    <a href="${pageContext.request.contextPath}/sports" class="hover:text-[#F4A300]">Sports</a>
                    <% 
                        String username = (String) session.getAttribute("username");
                        if (username != null) { 
                    %>
                        <span class="text-white">Welcome, <%= username %>!</span>
                        <a href="${pageContext.request.contextPath}/logout" class="hover:text-[#F4A300]">Logout</a>
                    <% } else { %>
                        <a href="${pageContext.request.contextPath}/login" class="bg-[#F4A300] text-white px-4 py-2 rounded-md hover:bg-[#A31621] transition duration-300">Login/Register</a>
                    <% } %>
                </div>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-7xl mx-auto px-4 py-8">
        <!-- Breadcrumb -->
        <div class="mb-6">
            <nav class="flex" aria-label="Breadcrumb">
                <ol class="inline-flex items-center space-x-1 md:space-x-3">
                    <li class="inline-flex items-center">
                        <a href="${pageContext.request.contextPath}/index" class="text-gray-600 hover:text-[#F4A300]">Home</a>
                    </li>
                    <li>
                        <div class="flex items-center">
                            <i class="fas fa-chevron-right text-gray-400 mx-2"></i>
                            <a href="${pageContext.request.contextPath}/sports" class="text-gray-600 hover:text-[#F4A300]">Sports</a>
                        </div>
                    </li>
                    <li>
                        <div class="flex items-center">
                            <i class="fas fa-chevron-right text-gray-400 mx-2"></i>
                            <span class="text-gray-500"><%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() : "Unknown Sport" %></span>
                        </div>
                    </li>
                </ol>
            </nav>
        </div>

        <!-- Sport Detail -->
        <div class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8 p-8">
                <!-- Image Gallery -->
                <div class="space-y-4">
                    <div class="relative h-96 rounded-lg overflow-hidden">
                        <img src="${pageContext.request.contextPath}<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getImage() != null ? ((Sport) request.getAttribute("sport")).getImage() : "/images/placeholder.jpg" : "/images/placeholder.jpg" %>" 
                             alt="<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() != null ? ((Sport) request.getAttribute("sport")).getName() : "Sport Image" : "Sport Image" %>" 
                             class="w-full h-full object-cover" 
                             onerror="this.src='https://via.placeholder.com/800'">
                        <div class="absolute top-4 right-4">
                            <button class="bg-white p-2 rounded-full shadow-lg hover:bg-gray-100">
                                <i class="fas fa-heart text-red-500"></i>
                            </button>
                        </div>
                    </div>
                    <div class="grid grid-cols-4 gap-4 image-gallery">
                        <img src="${pageContext.request.contextPath}<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getImage() != null ? ((Sport) request.getAttribute("sport")).getImage() : "/images/placeholder.jpg" : "/images/placeholder.jpg" %>" 
                             alt="<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() != null ? ((Sport) request.getAttribute("sport")).getName() : "Sport Image" : "Sport Image" %>" 
                             class="w-full h-24 object-cover rounded-lg cursor-pointer hover:opacity-75" 
                             onerror="this.src='https://via.placeholder.com/200'">
                        <img src="${pageContext.request.contextPath}<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getImage() != null ? ((Sport) request.getAttribute("sport")).getImage() : "/images/placeholder.jpg" : "/images/placeholder.jpg" %>" 
                             alt="<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() != null ? ((Sport) request.getAttribute("sport")).getName() : "Sport Image" : "Sport Image" %>" 
                             class="w-full h-24 object-cover rounded-lg cursor-pointer hover:opacity-75" 
                             onerror="this.src='https://via.placeholder.com/200'">
                        <img src="${pageContext.request.contextPath}<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getImage() != null ? ((Sport) request.getAttribute("sport")).getImage() : "/images/placeholder.jpg" : "/images/placeholder.jpg" %>" 
                             alt="<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() != null ? ((Sport) request.getAttribute("sport")).getName() : "Sport Image" : "Sport Image" %>" 
                             class="w-full h-24 object-cover rounded-lg cursor-pointer hover:opacity-75" 
                             onerror="this.src='https://via.placeholder.com/200'">
                        <img src="${pageContext.request.contextPath}<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getImage() != null ? ((Sport) request.getAttribute("sport")).getImage() : "/images/placeholder.jpg" : "/images/placeholder.jpg" %>" 
                             alt="<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() != null ? ((Sport) request.getAttribute("sport")).getName() : "Sport Image" : "Sport Image" %>" 
                             class="w-full h-24 object-cover rounded-lg cursor-pointer hover:opacity-75" 
                             onerror="this.src='https://via.placeholder.com/200'">
                    </div>
                </div>

                <!-- Sport Information -->
                <div class="space-y-6">
                    <div>
                        <h1 class="text-3xl font-bold text-[#002B5B]">
                            <%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getName() != null ? ((Sport) request.getAttribute("sport")).getName() : "Unnamed Sport" : "Unnamed Sport" %>
                        </h1>
                        <div class="flex items-center mt-2">
                            <div class="flex text-yellow-400">
                                <!-- Rating placeholder (Sport model doesn't have a rating field) -->
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                                <i class="fas fa-star"></i>
                            </div>
                            <span class="text-gray-600 ml-2">N/A (No reviews yet)</span>
                        </div>
                    </div>

                    <div class="space-y-4">
                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">Overview</h2>
                            <p class="text-gray-600 mt-2">
                                <%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getDescription() != null ? ((Sport) request.getAttribute("sport")).getDescription() : "No overview available." : "No overview available." %>
                            </p>
                        </div>

                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">Details</h2>
                            <div class="mt-2 space-y-2">
                                <div class="flex items-center text-gray-600">
                                    <i class="fas fa-trophy w-6"></i>
                                    <span>Status: <%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getStatus() != null ? ((Sport) request.getAttribute("sport")).getStatus() : "Unknown" : "Unknown" %></span>
                                </div>
                                <div class="flex items-center text-gray-600">
                                    <i class="fas fa-calendar w-6"></i>
                                    <span>Category: <%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getCategory() != null ? ((Sport) request.getAttribute("sport")).getCategory() : "Unknown" : "Unknown" %></span>
                                </div>
                                <div class="flex items-center text-gray-600">
                                    <i class="fas fa-map-marker-alt w-6"></i>
                                    <span>Location: N/A</span>
                                </div>
                            </div>
                        </div>

                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">History</h2>
                            <p class="text-gray-600 mt-2">
                                <%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getHistory() != null ? ((Sport) request.getAttribute("sport")).getHistory() : "No history available." : "No history available." %>
                            </p>
                        </div>

                        <div>
                            <h2 class="text-xl font-semibold text-[#002B5B]">Rules</h2>
                            <p class="text-gray-600 mt-2">
                                <%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getRules() != null ? ((Sport) request.getAttribute("sport")).getRules() : "No rules available." : "No rules available." %>
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Comments Section -->
            <div class="border-t border-gray-200 p-8">
                <h2 class="text-2xl font-semibold text-[#002B5B] mb-6">Comments</h2>
                
                <!-- Comment Form -->
                <div class="mb-8">
                    <form action="${pageContext.request.contextPath}/sport-detail" method="post">
                        <input type="hidden" name="sportId" value="<%= request.getAttribute("sport") != null ? ((Sport) request.getAttribute("sport")).getId() : "" %>">
                        <div>
                            <textarea name="commentText" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F4A300]" 
                                      rows="3" 
                                      placeholder="Write your comment..."></textarea>
                        </div>
                        <div class="flex justify-end mt-2">
                            <button type="submit" class="bg-[#F4A300] text-white px-6 py-2 rounded-md hover:bg-[#A31621] transition duration-300">
                                Post Comment
                            </button>
                        </div>
                        <% String error = (String) request.getAttribute("error");
                           if (error != null) { %>
                            <p class="text-red-500 mt-2"><%= error %></p>
                        <% } %>
                    </form>
                </div>

                <!-- Comments List -->
                <div class="space-y-6">
                    <%
                        List<SportComment> comments = (List<SportComment>) request.getAttribute("comments");
                        LocalDateTime nowDetail = LocalDateTime.now(ZoneId.of("Asia/Kathmandu"));
                        if (comments != null && !comments.isEmpty()) {
                            for (SportComment comment : comments) {
                                try {
                                    LocalDateTime commentTime = LocalDateTime.parse(comment.getCreatedAt(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                            .atZone(ZoneId.of("Asia/Kathmandu")).toLocalDateTime();
                                    long minutesAgo = ChronoUnit.MINUTES.between(commentTime, nowDetail);
                                    String timeAgo;
                                    if (minutesAgo < 60) {
                                        timeAgo = minutesAgo + " minutes ago";
                                    } else if (minutesAgo < 1440) {
                                        long hoursAgo = minutesAgo / 60;
                                        timeAgo = hoursAgo + " hours ago";
                                    } else {
                                        long daysAgo = minutesAgo / 1440;
                                        timeAgo = daysAgo + " days ago";
                                    }
                    %>
                    <div class="flex space-x-4">
                        <img src="https://ui-avatars.com/api/?name=<%= comment.getUsername() %>&background=002B5B&color=fff" 
                             alt="User" 
                             class="w-12 h-12 rounded-full">
                        <div class="flex-1">
                            <div class="flex items-center justify-between">
                                <h3 class="font-semibold text-[#002B5B]"><%= comment.getUsername() %></h3>
                                <span class="text-sm text-gray-500"><%= timeAgo %></span>
                            </div>
                            <p class="text-gray-600 mt-1"><%= comment.getCommentText() %></p>
                            <div class="flex items-center space-x-4 mt-2">
                                <button class="text-gray-500 hover:text-[#F4A300]">
                                    <i class="far fa-thumbs-up"></i> Like
                                </button>
                                <button class="text-gray-500 hover:text-[#F4A300]">
                                    <i class="far fa-comment"></i> Reply
                                </button>
                            </div>
                        </div>
                    </div>
                    <%      } catch (Exception e) { %>
                            <p class="text-red-500 text-sm">Error parsing timestamp for comment by <%= comment.getUsername() %>.</p>
                        <% }
                            }
                        } else { %>
                        <p class="text-gray-500 text-sm">No comments yet.</p>
                    <% } %>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="bg-[#002B5B] text-white mt-12">
        <div class="max-w-7xl mx-auto px-4 py-8">
            <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
                <div>
                    <h3 class="text-xl font-bold text-[#F4A300] mb-4">Nepal Navigator</h3>
                    <p class="text-gray-300">
                        Discover the rich culture, cuisine, and beauty of Nepal through our comprehensive guide.
                    </p>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Quick Links</h4>
                    <ul class="space-y-2">
                        <li><a href="${pageContext.request.contextPath}/foods" class="text-gray-300 hover:text-[#F4A300]">Foods</a></li>
                        <li><a href="${pageContext.request.contextPath}/attractions" class="text-gray-300 hover:text-[#F4A300]">Attractions</a></li>
                        <li><a href="${pageContext.request.contextPath}/music" class="text-gray-300 hover:text-[#F4A300]">Music</a></li>
                        <li><a href="${pageContext.request.contextPath}/movies" class="text-gray-300 hover:text-[#F4A300]">Movies</a></li>
                        <li><a href="${pageContext.request.contextPath}/sports" class="text-gray-300 hover:text-[#F4A300]">Sports</a></li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Contact Us</h4>
                    <ul class="space-y-2 text-gray-300">
                        <li><i class="fas fa-envelope mr-2"></i> info@nepalnavigator.com</li>
                        <li><i class="fas fa-phone mr-2"></i> +977 1234567890</li>
                        <li><i class="fas fa-map-marker-alt mr-2"></i> Kathmandu, Nepal</li>
                    </ul>
                </div>
                <div>
                    <h4 class="text-lg font-semibold mb-4">Follow Us</h4>
                    <div class="flex space-x-4">
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-facebook-f"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-twitter"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-instagram"></i>
                        </a>
                        <a href="#" class="text-gray-300 hover:text-[#F4A300]">
                            <i class="fab fa-youtube"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="border-t border-gray-700 mt-8 pt-8 text-center text-gray-300">
                <p>© 2025 Nepal Navigator. All rights reserved.</p>
            </div>
        </div>
    </footer>
</body>
</html>