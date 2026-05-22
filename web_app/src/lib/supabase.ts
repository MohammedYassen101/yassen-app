import { createClient } from '@supabase/supabase-js';

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL || 'https://ovjqwlbaobmntyisoqbw.supabase.co';
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY || 'sb_publishable_hJbw95RBuUoqXUQldBQyzQ_5QFOmw_r';

export const supabase = createClient(supabaseUrl, supabaseAnonKey);
