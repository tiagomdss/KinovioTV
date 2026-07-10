import { defineEventHandler, getCookie, deleteCookie } from 'h3';
import jwt from 'jsonwebtoken';
import { db } from '../../utils/db';
import { getJwtSecret } from '../../utils/auth';

export default defineEventHandler(async (event) => {
  const token = getCookie(event, 'jwt');

  if (!token) {
    return { loggedIn: false };
  }

  try {
    const decoded = jwt.verify(token, getJwtSecret()) as any;

    const user = await db.findUserById(decoded.id);
    if (!user) {
      deleteCookie(event, 'jwt');
      return { loggedIn: false };
    }

    return {
      loggedIn: true,
      user: {
        id: user.id,
        email: user.email,
        profiles: user.profiles,
        settings: user.settings
      }
    };
  } catch (error) {
    deleteCookie(event, 'jwt');
    return { loggedIn: false };
  }
});
